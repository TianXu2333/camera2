package com.tx.openglcamera.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View

/**
 * create by xu.tian
 * @date 2021/10/9
 */
class CircleImageView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    var mBitmap : Bitmap? = null
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mBitmap==null){
            return
        }
        var saveId = canvas!!.save()
        var matrix = Matrix()
        matrix.postTranslate(0f,((mBitmap!!.height/2-height/2).toFloat()))
        var path = Path()
        path.addCircle((width/2).toFloat(), (height/2).toFloat(), (width/2).toFloat(),Path.Direction.CW)
        canvas.clipPath(path)
        canvas!!.drawBitmap(mBitmap!!, matrix,null)
        canvas.restoreToCount(saveId)
    }

    fun setBitmap(bitmap:Bitmap){
        var bmpW = bitmap.width
        var bmpH = bitmap.height
        mBitmap = Bitmap.createScaledBitmap(bitmap,width,width*(bmpH/bmpW),false)
        postInvalidate()
    }
}