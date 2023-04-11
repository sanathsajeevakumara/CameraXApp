package com.sanathcoding.cameraxapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object CameraValues {
    public const val TAG = "cameraXApp"
    public const val FILE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    public val REQUIRED_PERMISSION =
        mutableListOf(
            android.Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    fun hasPermission(context: Context) = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}