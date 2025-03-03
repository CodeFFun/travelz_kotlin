package com.example.travelapp.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.travelapp.MainActivity
import com.example.travelapp.databinding.ActivityAddDestinationBinding
import com.example.travelapp.model.DestinationModel
import com.example.travelapp.repository.DestinationRepositoryImpl
import com.example.travelapp.utils.LoadingUtils
import com.example.travelapp.viewmodel.DestinationViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.FileOutputStream
import java.io.File

class AddDestination : AppCompatActivity() {
    lateinit var binding: ActivityAddDestinationBinding
    lateinit var destinationViewModel: DestinationViewModel
    lateinit var loadingUtils: LoadingUtils
    lateinit var ImageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddDestinationBinding.inflate(layoutInflater)
        var repo = DestinationRepositoryImpl(FirebaseAuth.getInstance())
        destinationViewModel = DestinationViewModel(repo)


        binding.cancelAddDestinatinButton.setOnClickListener{
            val intent = Intent(this@AddDestination,
                MainActivity::class.java)
            startActivity(intent)
        }

        loadingUtils = LoadingUtils(this)
        setContentView(binding.root)

        binding.addImageButton.setOnClickListener{
            selectImage()
        }

        binding.addDestinationButton.setOnClickListener {
            val title = binding.addDestinationTitle.text.toString()
            val desc = binding.addDestinationDesc.text.toString()
            val date = binding.addDestinationDate.text.toString()
            val location = binding.addDestinationLocation.text.toString()

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (userId.isEmpty()) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if image is selected
            if (!::ImageUri.isInitialized) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadingUtils.show() // Show loading indicator

            val destination = DestinationModel(
                // Let repository generate ID - remove this parameter or pass empty string
                userId = userId,
                title = title,
                desc = desc,
                date = date,
                location = location,
                imageUri = ImageUri.toString(),
                imageName = getFileName(ImageUri)
            )

            destinationViewModel.addDestination(userId, destination) { success, message ->
                loadingUtils.dismiss() // Hide loading indicator
                if (success) {
                    Toast.makeText(this, "Destination added!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddDestination,
                        MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data?.data != null) {
            val originalUri = data.data!!

            try {
                // Compress the image
                ImageUri = compressImage(originalUri)

                // Display the compressed image
                binding.destinationAddImg.setImageURI(ImageUri)
            } catch (e: Exception) {
                Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddDestination", "Image processing error", e)
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                cursor.moveToFirst()
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
    // Add this function to your AddDestination class
    private fun compressImage(uri: Uri): Uri {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        // Calculate new dimensions while maintaining aspect ratio
        val maxDimension = 1024 // Max width or height in pixels
        val scale: Float
        val newWidth: Int
        val newHeight: Int

        if (bitmap.width > bitmap.height) {
            scale = maxDimension.toFloat() / bitmap.width
            newWidth = maxDimension
            newHeight = (bitmap.height * scale).toInt()
        } else {
            scale = maxDimension.toFloat() / bitmap.height
            newWidth = (bitmap.width * scale).toInt()
            newHeight = maxDimension
        }

        // Create resized bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        // Compress to file
        val outputFile = File(cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(outputFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.close()

        return Uri.fromFile(outputFile)
    }
}


