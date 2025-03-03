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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.travelapp.MainActivity
import com.example.travelapp.R
import com.example.travelapp.databinding.ActivityAddDestinationBinding
import com.example.travelapp.databinding.ActivityUpdateDestinationBinding
import com.example.travelapp.model.DestinationModel
import com.example.travelapp.repository.DestinationRepositoryImpl
import com.example.travelapp.ui.activity.AddDestination
import com.example.travelapp.utils.LoadingUtils
import com.example.travelapp.viewmodel.DestinationViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class UpdateDestination : AppCompatActivity() {
    lateinit var binding: ActivityUpdateDestinationBinding
    lateinit var destinationViewModel: DestinationViewModel
    lateinit var loadingUtils: LoadingUtils
    lateinit var ImageUri: Uri



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUpdateDestinationBinding.inflate(layoutInflater)
        var repo = DestinationRepositoryImpl(FirebaseAuth.getInstance())
        destinationViewModel = DestinationViewModel(repo)

        val destinationId = intent.getStringExtra("destinationId") ?: ""

        destinationViewModel.destination.observe(this, Observer { destination ->
            destination?.let {
                binding.updateDestinationTitle.setText(it.title)
                binding.updateDestinationDesc.setText(it.desc)
                binding.updtaeDestinationDate.setText(it.date)
                binding.updateDestinationLocation.setText(it.location)

                // Optionally, load image if you have an image URI (using Picasso or Glide)
                if (it.imageUri.isNotEmpty()) {
                    ImageUri = Uri.parse(it.imageUri)
                    binding.destinationUpdateImg.setImageURI(ImageUri)
                }
            }
        })

        destinationViewModel.operationStatus.observe(this, Observer { (success, message) ->
            if (!success) {
                Toast.makeText(this, "Failed to load destination: $message", Toast.LENGTH_SHORT).show()
            }
        })

        destinationViewModel.operationStatus.observe(this, Observer { (success, message) ->
            if (!success) {
                Toast.makeText(this@UpdateDestination, "Failed: $message", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@UpdateDestination, "destinationId", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch the destination data
        destinationViewModel.getDestination(destinationId)

        binding.cancelUpdateDestination.setOnClickListener{
            val intent = Intent(this@UpdateDestination,
                MainActivity::class.java)
            startActivity(intent)
        }

        binding.updateImageButton.setOnClickListener{
            selectImage()
        }

        loadingUtils = LoadingUtils(this)
        setContentView(binding.root)

        binding.updateDestinationButton.setOnClickListener{
            val title = binding.updateDestinationTitle.text.toString()
            val desc = binding.updateDestinationDesc.text.toString()
            val date = binding.updtaeDestinationDate.text.toString()
            val location = binding.updateDestinationLocation.text.toString()

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

            val data = mutableMapOf<String, Any>(
                "title" to title,
                "desc" to desc,
                "date" to date,
                "location" to location,
                "imageUri" to ImageUri.toString(),  // If you want to include imageUri
                "imageName" to getFileName(ImageUri)  // If you want to include imageName
            )

            destinationViewModel.editDestination(userId, destinationId, data)

// Observe the operationStatus LiveData to get the success message
            destinationViewModel.operationStatus.observe(this, Observer { (success, message) ->
                loadingUtils.dismiss() // Hide loading indicator
                if (success) {
                    Toast.makeText(this, "Destination Updated!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UpdateDestination, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed: $message", Toast.LENGTH_SHORT).show()
                }
            })
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
                binding.destinationUpdateImg.setImageURI(ImageUri)
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