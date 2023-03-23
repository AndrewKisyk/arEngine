package com.android.arengiene

import android.view.SurfaceView
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat

interface ArEngineController {
    fun initEngine(javaCameraView: JavaCameraView)
    fun destroyEngine()

    class ArEngineControllerImpl: ArEngineController {
        private var mRGBA: Mat? = null
        private var cameraBridge: CameraBridgeViewBase? = null
        
        private val cvCameraViewListener = object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) = Unit

            override fun onCameraViewStopped() = Unit

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat? {
                mRGBA = inputFrame?.rgba()
                return mRGBA
            }
        }
        override fun initEngine(javaCameraView: JavaCameraView) {
            if (!OpenCVLoader.initDebug()) {
                System.loadLibrary("opencv_java4")
            }
            System.loadLibrary("native-lib")
            initCameraBridge(javaCameraView)
            initCamera(javaCameraView, CameraBridgeViewBase.CAMERA_ID_BACK)
        }
        
        private fun initCameraBridge(cameraBridge: CameraBridgeViewBase) {
            this.cameraBridge = cameraBridge
            cameraBridge.enableView()
            cameraBridge.visibility = SurfaceView.VISIBLE
            cameraBridge.setCvCameraViewListener(cvCameraViewListener)
        }
        

        private fun initCamera(javaCameraView: JavaCameraView, activeCamera: Int) {
            javaCameraView.setCameraIndex(activeCamera)
            javaCameraView.visibility = CameraBridgeViewBase.VISIBLE
            javaCameraView.setCvCameraViewListener(cvCameraViewListener)
        }

        override fun destroyEngine() {
            cameraBridge?.disableView()
            mRGBA = null
        }

        external fun salt(matAddrGray: Long, nbrElem: Int)
    }

}