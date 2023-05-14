package com.android.arengiene

import android.view.SurfaceView
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

interface ArEngineController {
    fun initEngine(javaCameraView: JavaCameraView)
    fun destroyEngine()

    class ArEngineControllerImpl : ArEngineController {
        private var cameraBridge: CameraBridgeViewBase? = null
        private val humanDetector: HumanDetector = HumanDetector.HumanDetectorImpl()

        private val cvCameraViewListener = object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) = Unit

            override fun onCameraViewStopped() = Unit

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat? {
                val mRGBA = inputFrame?.rgba() ?: return null
                Core.flip(mRGBA.t(), mRGBA, 1)
                val rect = humanDetector.detectHuman(mRGBA)
                rect?.let { drawRect(mRGBA, it) }
                Core.flip(mRGBA.t(), mRGBA, 0)
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
        }

        private fun drawRect(mat: Mat, rect: Rect) {
            val thickness = 2
            val color = Scalar(255.0, 0.0, 0.0)
            Imgproc.rectangle(mat, rect.tl(), rect.br(), color, thickness)
        }
    }

}