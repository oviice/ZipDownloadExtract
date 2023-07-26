package com.ovi.zipdownloadextract
import android.content.Context
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ZipExtractor {

    private const val TAG = "ZipExtractor"

    interface ZipExtractListener {
        fun onZipExtracted(success: Boolean)
    }

    fun downloadAndExtractZip(context: Context, zipUrl: String, outputDirectory: String, listener: ZipExtractListener) {
        DownloadAndExtractTask(context, zipUrl, outputDirectory, listener).execute()
    }

    private class DownloadAndExtractTask(
        private val context: Context,
        private val zipUrl: String,
        private val outputDirectory: String,
        private val listener: ZipExtractListener
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            try {
                // Step 1: Download the zip file
                val url = URL(zipUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.connect()

                val input = BufferedInputStream(conn.inputStream)

                // Create the output directory if it doesn't exist
                val dir = File(context.filesDir, outputDirectory)
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "Failed to create output directory")
                        return false
                    }
                }

                // Step 2: Save the zip file
                val zipFilePath = "${dir.absolutePath}${File.separator}downloaded.zip"
                val output = FileOutputStream(zipFilePath)

                val data = ByteArray(1024)
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                input.close()

                // Step 3: Extract the zip file
                unzip(zipFilePath, dir.absolutePath)

                // Step 4: Clean up - delete the zip file
                val zipFile = File(zipFilePath)
                if (zipFile.exists()) {
                    zipFile.delete()
                }

                return true
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }

        override fun onPostExecute(success: Boolean) {
            super.onPostExecute(success)
            listener.onZipExtracted(success)
        }
        fun unzip(zipFilePath: String, outputDirectory: String) {
            try {
                val inputStream = FileInputStream(zipFilePath)
                val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))
                var zipEntry: ZipEntry? = zipInputStream.nextEntry

                val buffer = ByteArray(1024)
                var count: Int
                while (zipEntry != null) {
                    val file = File(outputDirectory, zipEntry.name)

                    if (zipEntry.isDirectory) {
                        file.mkdirs()
                    } else {
                        val parent = file.parentFile
                        if (!parent.exists()) {
                            parent.mkdirs()
                        }

                        val outputStream = FileOutputStream(file)
                        while (zipInputStream.read(buffer).also { count = it } != -1) {
                            outputStream.write(buffer, 0, count)
                        }
                        outputStream.close()
                    }
                    zipEntry = zipInputStream.nextEntry
                }

                zipInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

