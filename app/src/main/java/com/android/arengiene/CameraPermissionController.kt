package com.android.arengiene

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

interface CameraPermissionController {
    fun requestPermission()
    fun permissionGranted()
    fun permissionDenied()

    interface CameraPermissionFlow {
        fun doOnPermissionGranted()
        fun doOnPermissionDenied()
    }

    class CameraPermissionControllerImpl constructor(
        private val context: Context,
        private val cameraPermissionFlow: CameraPermissionFlow,
        private val activityResultLauncher: ActivityResultLauncher<String>
    ) : CameraPermissionController {
        override fun requestPermission() {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED
            ) {
                activityResultLauncher.launch(Manifest.permission.CAMERA)
            } else {
                cameraPermissionFlow.doOnPermissionGranted()
            }
        }

        override fun permissionGranted() {
            cameraPermissionFlow.doOnPermissionGranted()
        }

        override fun permissionDenied() {
            cameraPermissionFlow.doOnPermissionDenied()
        }
    }
}