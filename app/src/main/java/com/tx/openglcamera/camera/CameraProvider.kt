package com.tx.openglcamera.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.lang.Exception

/**
 * create by xu.tian
 * @date 2021/10/9
 */
class CameraProvider(var context: Context, var captureTexture: SurfaceTexture) {
    private val tag = CameraProvider::class.java.simpleName
    private var handlerThread: HandlerThread = HandlerThread("CameraProvider")
    private var handler: Handler
    private var mCameraId = "0"
    private var cameraManager: CameraManager? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    var previewWidth = 0
    var previewHeight = 0
    private var cameraDevice: CameraDevice? = null
    private var captureCallBack: CaptureCallBack? = null

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mCameraId = getCamera()[0]
        initDefaultPreviewSize()
    }

    /**
     * 获取CameraId
     */
    fun getCamera(): Array<out String> {
        return cameraManager!!.cameraIdList
    }

    /**
     * 获取当前camera的支持预览大小
     */
    fun getPreviewSize(): Array<out Size>? {
        var characteristics = cameraManager?.getCameraCharacteristics(mCameraId)
        val configs = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        return configs!!.getOutputSizes(SurfaceTexture::class.java)
    }

    /**
     * 根据cameraId打开指定的摄像头
     */
    fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        createImageReader()
        cameraManager?.openCamera(mCameraId, cameraStateCallback, handler)
    }

    /**
     *  打开Camera的状态回调
     */
    var cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(p0: CameraDevice) {
            log("cameraStateCallback onOpened")
            cameraDevice = p0
            createCaptureSession()
        }

        override fun onDisconnected(p0: CameraDevice) {
            log("cameraStateCallback onDisconnected")
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            log("cameraStateCallback onError")
        }
    }

    /**
     * 创建CameraCaptureSession的状态回调
     */
    var captureSessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(p0: CameraCaptureSession) {
            captureSession = p0
            var captureSessionRequest =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureSessionRequest.addTarget(Surface(captureTexture))
            var request = captureSessionRequest.build()
            captureSession!!.setRepeatingRequest(request, null, handler)
        }

        override fun onConfigureFailed(p0: CameraCaptureSession) {
            log("captureSessionCallback onConfigureFailed")
        }
    }

    /**
     * ImageReader获取到数据时的回调
     */
    private var imageAvailableListener = ImageReader.OnImageAvailableListener {
        var image = imageReader!!.acquireLatestImage()
        var buffer = image.planes[0].buffer
        var length = buffer.remaining()
        var bytes = ByteArray(length)
        buffer.get(bytes)
        image.close()
        try {
            var bmp = BitmapFactory.decodeByteArray(bytes, 0, length, null)
            var matrix = Matrix()
            matrix.postRotate(90f)
            var bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, false)
            if (captureCallBack != null) {
                captureCallBack!!.onSucceed(bitmap)
            }
        } catch (e: Exception) {
            if (captureCallBack != null) {
                captureCallBack!!.onFailed(Throwable(e.localizedMessage))
            }
        }

    }

    fun changePreviewSize(width: Int, height: Int) {
        previewWidth = width
        previewHeight = height
        if (cameraDevice != null) {
            createImageReader()
            createCaptureSession()
        }
    }

    private fun createCaptureSession() {
        captureTexture!!.setDefaultBufferSize(previewWidth, previewHeight);//设置SurfaceTexture缓冲区大小
        if (captureSession != null) {
            captureSession!!.close()
            captureSession = null
        }
        cameraDevice!!.createCaptureSession(
            listOf(
                Surface(captureTexture),
                (imageReader!!.surface)
            ), captureSessionCallback, handler
        )
    }

    private fun createImageReader() {
        if (imageReader != null) {
            imageReader!!.close()
            imageReader = null
        }
        imageReader = ImageReader.newInstance(previewWidth, previewHeight, ImageFormat.JPEG, 2)
        imageReader!!.setOnImageAvailableListener(imageAvailableListener, handler)
    }

    fun openCamera(cameraId: String) {
        if (cameraDevice != null) {
            releaseCamera()
        }
        mCameraId = cameraId
        initDefaultPreviewSize()
        openCamera()
    }

    private fun initDefaultPreviewSize() {
        var size = getPreviewSize()!![0]
        previewWidth = size.width
        previewHeight = size.height
    }

    fun capturePic(captureCallBack: CaptureCallBack) {
        if (cameraDevice == null) {
            return
        }
        this.captureCallBack = captureCallBack
        try {
            var requestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            requestBuilder.addTarget(imageReader!!.surface)
            var request = requestBuilder.build()
            captureSession!!.capture(request, null, handler)
        } catch (e: Exception) {
            if (captureCallBack != null) {
                captureCallBack.onFailed(Throwable(e.localizedMessage))
            }
            Toast.makeText(context, "${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }

    }

    fun releaseCamera() {
        captureSession!!.close()
        cameraDevice!!.close()
    }

    fun destroy() {
        handlerThread.quit()
    }

    private fun log(string: String) {
        Log.d(tag, string)
    }
}