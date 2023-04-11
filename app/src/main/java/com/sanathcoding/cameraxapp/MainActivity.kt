package com.sanathcoding.cameraxapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.view.LifecycleCameraController
import com.sanathcoding.cameraxapp.CameraValues.REQUIRED_PERMISSION
import com.sanathcoding.cameraxapp.CameraValues.hasPermission
import com.sanathcoding.cameraxapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    // If using cameraController
    private lateinit var cameraController: LifecycleCameraController

    // If using Camera Provide
//    private val imageProvider: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // request camera related permission
        if (!hasPermission(baseContext)) activityResultLauncher.launch(REQUIRED_PERMISSION)
//        else startCamera()
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
//            else startCamera()
        }
    }
}