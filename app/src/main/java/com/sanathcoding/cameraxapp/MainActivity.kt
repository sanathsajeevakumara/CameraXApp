package com.sanathcoding.cameraxapp

import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sanathcoding.cameraxapp.CameraValues.FILE_FORMAT
import com.sanathcoding.cameraxapp.CameraValues.REQUIRED_PERMISSION
import com.sanathcoding.cameraxapp.CameraValues.TAG
import com.sanathcoding.cameraxapp.CameraValues.hasPermission
import com.sanathcoding.cameraxapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    // If using cameraController
//    private lateinit var cameraController: LifecycleCameraController

    // If using Camera Provide
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // request camera related permission
        if (!hasPermission(baseContext)) activityResultLauncher.launch(REQUIRED_PERMISSION)
        else lifecycleScope.launch { startCamera() }

        viewBinding.imageCaptureBtn.setOnClickListener { takePhoto() }

    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        // Handle Permission Granted/Rejected
        var permissionGranted = true
        permission.entries.forEach {
            if (it.key in REQUIRED_PERMISSION && !it.value) permissionGranted = false

            if (!permissionGranted) Toast.makeText(
                this,
                "Permission request denied",
                Toast.LENGTH_LONG
            ).show()
            else lifecycleScope.launch { startCamera() }
        }
    }

    private suspend fun startCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()

        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)

        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            var camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, imageCapture
            )

        } catch (e: Exception) {
            Log.d(TAG, "UseCase binding failed!", e)
        }
    }

    private fun takePhoto() {
        // Create time Stamped name and MediaStore entry
        val name = SimpleDateFormat(FILE_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValue = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image.jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        // Create output options object which contain file + metadata
        val outPutOption = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValue
        ).build()

        // set up the image capture listener Which is trigger after the image captured
        imageCapture?.takePicture(
            outPutOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                    val msg = "Photo captured successfully ${results.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }

                override fun onError(e: ImageCaptureException) {
                    Log.d(TAG, "Photo capture failed: ${e.message}", e)
                }

            }
        )

    }
}