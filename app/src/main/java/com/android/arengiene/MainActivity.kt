package com.android.arengiene

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.android.arengiene.opencvnativeandroidstudio.R

class MainActivity : AppCompatActivity() {
   
    private val arEngineController: ArEngineController = ArEngineController.ArEngineControllerImpl()
    
    private val cameraPermissionFlow = object : CameraPermissionController.CameraPermissionFlow {
        override fun doOnPermissionGranted() {
            arEngineController.initEngine(findViewById(R.id.main_surface))
        }

        override fun doOnPermissionDenied() {
            cameraPermissionController.requestPermission()
        }
    }

    private val activityResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraPermissionController.permissionGranted()
            } else {
                cameraPermissionController.permissionDenied()
            }
        }


    private val cameraPermissionController: CameraPermissionController =
        CameraPermissionController.CameraPermissionControllerImpl(
            this,
            cameraPermissionFlow,
            activityResultLauncher
        )

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onResume() {
        super.onResume()
        cameraPermissionController.requestPermission()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
    }

    public override fun onPause() {
        super.onPause()
        arEngineController.destroyEngine()
    }

    public override fun onDestroy() {
        super.onDestroy()
        arEngineController.destroyEngine()
    }
}