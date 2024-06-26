package com.dicoding.picodiploma.loginwithanimation.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.Result
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.getImageUri
import com.dicoding.picodiploma.loginwithanimation.data.retrorfit.uriToFile
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileNotFoundException

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.progressBar.isVisible = true
            Glide.with(this)
                .load(it)
                .into(binding.previewImageView)
            binding.progressBar.isVisible = false
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            try {
                val imageFile = uriToFile(uri, this)

                lifecycleScope.launch {
                    val compressedImageFile = Compressor.compress(this@AddActivity, imageFile)

                    Log.d("Image File", "Compressed Image: ${compressedImageFile.path}")

                    val desc = binding.descEditText.text.toString()
                    if (desc.isEmpty()) {
                        showToast("Description cannot be empty")
                        return@launch
                    }

                    val requestBody = desc.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = compressedImageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        compressedImageFile.name,
                        requestImageFile
                    )

                    viewModel.addStory(multipartBody, requestBody).observe(this@AddActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.isVisible = true
                            }

                            is Result.Success<*> -> {
                                viewModel.responseLiveData.postValue(true)
                            }

                            is Result.Error -> {
                                viewModel.responseLiveData.postValue(false)
                            }
                        }
                    }

                    viewModel.responseLiveData.observe(this@AddActivity) { isSuccess ->
                        binding.progressBar.isVisible = false
                        if (isSuccess) {
                            showAlertDialog("Success", "Data sent successfully.")
                        } else {
                            showAlertDialog("Failed", "Failed to send data.")
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                showToast("Image file not found")
            }
        } ?: showToast("Image cannot be empty")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(getString(R.string.continue_text)) { _, _ ->
                val intent = Intent(this@AddActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
