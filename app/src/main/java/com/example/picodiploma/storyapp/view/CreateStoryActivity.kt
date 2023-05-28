package com.example.picodiploma.storyapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.storyapp.createCustomTempFile
import com.dicoding.picodiploma.storyapp.reduceFileImage
import com.dicoding.picodiploma.storyapp.uriToFile
import com.example.picodiploma.storyapp.data.ApiServiceHelper
import com.example.picodiploma.storyapp.databinding.ActivityCreateStoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private lateinit var apiServiceHelper: ApiServiceHelper

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        apiServiceHelper = ApiServiceHelper(getToken())
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        binding.imageViewPreview.setOnClickListener { startTakePhoto() }
        binding.btnPost.setOnClickListener { uploadImage() }
        binding.btnSelectFromGallery.setOnClickListener {
            startSelectFromGallery()
        }
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            } else {
                locationManager.removeUpdates(locationListener)
                currentLocation = null
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@CreateStoryActivity,
                "com.picodiploma.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)
                myFile.let { file ->
                    getFile = file
                    binding.imageViewPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }
        }

    private fun startSelectFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        launcherIntentGallery.launch(intent)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImage: Uri? = result.data?.data
                if (selectedImage != null) {
                    val file = uriToFile(selectedImage, this)
                    getFile = file
                    binding.imageViewPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
                }
            }
        }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description =
                binding.editTextPostStory.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val lat = currentLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val lon = currentLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val uploadStoryRequest = apiServiceHelper.uploadStory(
                        description,
                        file,
                        lat,
                        lon
                    )
                    val response = withContext(Dispatchers.IO) {
                        uploadStoryRequest
                    }
                    if (response.error) {
                        Toast.makeText(
                            this@CreateStoryActivity,
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@CreateStoryActivity,
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@CreateStoryActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@CreateStoryActivity,
                        "Failed to upload story: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this@CreateStoryActivity,
                "Please insert image first",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }
}
