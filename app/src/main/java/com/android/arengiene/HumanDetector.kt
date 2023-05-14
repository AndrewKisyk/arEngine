package com.android.arengiene

import kotlinx.coroutines.*
import org.opencv.core.*
import kotlin.coroutines.CoroutineContext

interface HumanDetector {

    fun detectHuman(mat: Mat): Rect?

    class HumanDetectorImpl : HumanDetector, CoroutineScope {
        override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
        private var passNewFrame: (mat: Mat) -> Unit = ::doOnNewFrame
        private var rect: Rect? = null

        override fun detectHuman(mat: Mat): Rect? {
            passNewFrame.invoke(mat)
            return rect
        }

        private fun doOnNewFrame(mat: Mat) {
            passNewFrame = {}
            launch {
                rect = detectHuman(mat.nativeObjAddr)
                passNewFrame = ::doOnNewFrame
            }
        }

        private suspend fun detectHuman(matAddr: Long): Rect? {
            return withContext(Dispatchers.Default) {
                humanDetection(matAddr)
            }
        }

        private external fun humanDetection(matAddr: Long): Rect?
    }
}



