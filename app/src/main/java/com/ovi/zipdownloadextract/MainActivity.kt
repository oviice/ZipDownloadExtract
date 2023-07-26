package com.ovi.zipdownloadextract

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.ovi.zipdownloadextract.ZipExtractor.downloadAndExtractZip
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val download: Button = findViewById(R.id.download)
        val show: Button = findViewById(R.id.show)
        val delete: Button = findViewById(R.id.delete)
        val image: ImageView = findViewById(R.id.image)

        download.setOnClickListener {

            val zipUrl = "https://celbd.com/TCPLOS/smart/drawable.zip"
            val outputDirectory =
                "my_file" // Output directory where the contents will be extracted

            downloadAndExtractZip(
                applicationContext,
                zipUrl,
                outputDirectory,
                object : ZipExtractor.ZipExtractListener {
                    override fun onZipExtracted(success: Boolean) {
                        if (success) {
                            // Zip file downloaded and extracted successfully
                            // You can now access the extracted files in the specified output directory.
                            Toast.makeText(this@MainActivity,"zip extract done",Toast.LENGTH_SHORT).show()
                        } else {
                            // There was an error in the process
                        }
                    }
                })

        }
        show.setOnClickListener {
            val drawable = loadDrawableFromStorage("app_logo.webp")

            // Display the drawable image in an ImageView
            image.setImageDrawable(drawable)
        }

        delete.setOnClickListener{
            val dir = File(this.filesDir, "my_file/drawable")
            val isDeleted = deleteFolder(dir)

            if (isDeleted) {
                // Folder and its contents were successfully deleted
                Toast.makeText(this,"folder delete",Toast.LENGTH_SHORT).show()
            } else {
                // Failed to delete the folder and its contents
            }
        }

    }
    fun deleteFolder(file: File): Boolean {
        if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null) {
                for (child in children) {
                    deleteFolder(child)
                }
            }
        }
        return file.delete()
    }
    private fun loadDrawableFromStorage(imageFileName: String): Drawable? {
        val dir = File(this.filesDir, "my_file/drawable")

        val path = "${dir}/$imageFileName"
        return Drawable.createFromPath(path)

    }
}