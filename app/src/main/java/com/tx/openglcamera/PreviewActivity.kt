package com.tx.openglcamera

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tx.openglcamera.camera.CameraProvider
import com.tx.openglcamera.camera.CaptureCallBack
import com.tx.openglcamera.databinding.ActivityPreviewBinding
import com.tx.openglcamera.utils.ImageUtil
import com.tx.openglcamera.view.SettingPanel
import com.tx.txcustomview.view.ShutterTouchEventListener
import java.lang.Exception


/**
 * create by xu.tian
 * @date 2021/10/9
 */
class PreviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPreviewBinding
    private lateinit var cameraProvider : CameraProvider
    private lateinit var mContext :Context
    private var previewSizeCount = 0
    private var currentCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this
        binding.previewView.surfaceTextureListener = object : TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                cameraProvider = CameraProvider(mContext!!,p0)
                cameraProvider.openCamera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {

            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {

            }

        }

        binding.shutterBtn.listener = object : ShutterTouchEventListener{
            override fun takePicture() {
                cameraProvider.capturePic(object : CaptureCallBack{
                    override fun onSucceed(bitmap: Bitmap) {
                        binding.capture.setBitmap(bitmap)
                        ImageUtil.save(mContext,bitmap,"${java.util.Date().time}")
                    }

                    override fun onFailed(e: Throwable) {

                    }

                })
            }
            override fun videoStart() {

            }

            override fun videoEnd() {

            }

        }
        binding.toggleCamera.setOnClickListener {
            toggleCamera()
        }
        binding.capture.setOnClickListener{
            openAlbum()
        }
        binding.settingsBtn.setOnClickListener{
            showSetting()
        }
    }

    override fun onResume() {
        super.onResume()
        initCapture()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider.releaseCamera()
        cameraProvider.destroy()
    }

    private fun toggleCamera(){
        currentCount++
        var objectValueAnimator = ObjectAnimator.ofFloat(binding.toggleCamera,"rotation",0f,180f)
        objectValueAnimator.duration = 300L
        objectValueAnimator.start()
        cameraProvider.openCamera("${currentCount%2}")
    }

    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT;
        startActivity(intent)
    }

    private fun initCapture(){
        var lastPic = ImageUtil.getLatestPhoto(this)
        var bitmap : Bitmap
        try {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(lastPic!!.second,options)
        }catch (e : Exception){
            Toast.makeText(this,"${e.localizedMessage}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSetting(){
        var settingPanel = SettingPanel(this,cameraProvider)
        settingPanel.showAtLocation(binding.previewView,Gravity.TOP,0,0)
    }

}