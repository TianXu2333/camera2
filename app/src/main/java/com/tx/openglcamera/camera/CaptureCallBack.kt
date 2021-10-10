package com.tx.openglcamera.camera

import android.graphics.Bitmap

/**
 * create by xu.tian
 * @date 2021/10/10
 */
interface CaptureCallBack {
    fun onSucceed(bitmap: Bitmap)
    fun onFailed(e: Throwable)
}