package com.tx.txcustomview.view

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

/**
 * create by xu.tian
 * @date 2021/9/9
 */
class ShutterView : View{
    // 定义当前的操作
    companion object{
        const val unknownOp = 0
        const val takePhotoOp = 1
        const val takeVideoOp = 2
    }
    var option = unknownOp

    private var paint = Paint()
    var listener : ShutterTouchEventListener?
    init {
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = 15f
        listener = null
    }
    // 开始按下去的动画
    private lateinit var pictureAnimator : ValueAnimator
    private var currentPictureValue = 0f
    private var pictureDuration = 500L

    // 长按执行到Video录制的动画
    lateinit var videoAnimator : ValueAnimator
    private var currentVideoValue = 0f
    private var videoDuration = 15000L

    // 取消操作时的动画
    lateinit var cancelAnimator : ValueAnimator
    private var currentCancelValue = 0f
    private var cancelDuration = 200L

    // 圆心x坐标
    var centerX = 0f
    // 圆心y坐标
    var centerY = 0f
    // 初始半径
    var radius = 0f
    // 绘制的半径
    var drawRadius = 0f
    // 缩小的半径的最小值
    var minRadius = 0f
    // 缩小的半径的最大值
    var maxRadius = 0f
    // 画笔的不透明度
    var paintAlpha = 255
    // 拍照或者录像动画结束时的半径
    var animEndRadius = 0f

    constructor(context: Context): super(context)

    constructor(context: Context,attributeSet: AttributeSet): super(context,attributeSet){
        initPictureAnim()
        initVideoAnim()
        initCancelAnim()
        setLayerType(LAYER_TYPE_SOFTWARE,null)
        rotation = -90f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when(option) {
            unknownOp -> drawUnknownOp(canvas)
            takePhotoOp -> drawTakePicture(canvas)
            takeVideoOp -> drawTakeVideo(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = (w/2).toFloat()
        centerY = (h/2).toFloat()
        radius = if (centerX<centerY){
            centerX/10*6
        }else{
            centerY/10*6
        }
        drawRadius = radius
        minRadius = centerX/10*5
        maxRadius = centerX/10*8
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> actionDown()
            MotionEvent.ACTION_UP -> actionUp()
        }
        return true
    }

    private fun initPictureAnim(){
        pictureAnimator = ValueAnimator.ofFloat(0F, 100F)
        pictureAnimator.duration = pictureDuration
        pictureAnimator.addUpdateListener { valueAnimator ->
            currentPictureValue = valueAnimator.animatedValue as Float
           if (currentPictureValue<100F/4){
               drawRadius =  radius-(radius-minRadius)*(currentPictureValue/(100f/4))
               paintAlpha =255
            }else if (currentPictureValue>100F/4 && currentPictureValue<(100F)/4*3){
               drawRadius =  minRadius
               paintAlpha = 255
            }else{
               drawRadius =  minRadius + (maxRadius-minRadius)*((currentPictureValue-(100f/4*3))/(100f/4))
               paintAlpha = (255-205*(currentPictureValue-100f/4*3)/(100f/4)).toInt()
            }
            postInvalidate()
        }
        pictureAnimator.addListener(object  : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
                option = takePhotoOp
            }

            override fun onAnimationEnd(p0: Animator?) {
                animEndRadius = drawRadius
                if (option != unknownOp){
                    videoAnimator.start()
                }else{
                    cancelAnimator.start()
                }

            }

            override fun onAnimationCancel(p0: Animator?) {
                drawRadius = radius
                if (listener!=null){
                    listener!!.takePicture()
                }
                option = unknownOp
            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }



    private fun initVideoAnim(){
        videoAnimator = ValueAnimator.ofFloat(0F,100F)
        videoAnimator.duration = videoDuration
        videoAnimator.addUpdateListener { valueAnimator ->
            currentVideoValue = valueAnimator.animatedValue as Float
            postInvalidate()
        }
        videoAnimator.addListener(object  : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
                option = takeVideoOp
                if (listener!=null){
                    listener!!.videoStart()
                }
            }

            override fun onAnimationEnd(p0: Animator?) {
                if (listener!=null){
                    listener!!.videoEnd()
                }
                option = unknownOp
                animEndRadius = drawRadius
                cancelAnimator.start()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }
    private fun initCancelAnim(){
        cancelAnimator = ValueAnimator.ofFloat(0f,100f)
        cancelAnimator.duration = cancelDuration
        cancelAnimator.addUpdateListener { valueAnimator ->
            currentCancelValue = valueAnimator.animatedValue as Float
            drawRadius = if (animEndRadius>radius){
                animEndRadius - (animEndRadius - radius)*(currentCancelValue/100f)
            }else {
                animEndRadius + (animEndRadius - radius)*(currentCancelValue/100f)
            }
            postInvalidate()
        }
        cancelAnimator.addListener(object  : Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {

            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    private fun actionDown(){
        pictureAnimator.start()
    }

    private fun actionUp(){
        if(option == takePhotoOp){
            pictureAnimator.cancel()
        }else{
            videoAnimator.cancel()
        }
    }

    private fun drawUnknownOp(canvas: Canvas){
        paint.color = Color.WHITE
        paint.alpha = 255
        canvas.drawCircle(centerX,centerY,drawRadius,paint)
    }

    private fun drawTakePicture(canvas: Canvas){
        paint.color = Color.WHITE
        paint.alpha = paintAlpha
        canvas.drawCircle(centerX,centerY,drawRadius,paint)
    }

    private fun drawTakeVideo(canvas: Canvas){
        var path = Path()
        path.addCircle(centerX,centerY,maxRadius,Path.Direction.CW)
        var pathMeasure  = PathMeasure()
        pathMeasure.setPath(path,true)
        var currentPath = Path()
        var leftPath = Path()
        pathMeasure.getSegment(0F,currentVideoValue/100*pathMeasure.length,currentPath,true)
        pathMeasure.getSegment(currentVideoValue/100*pathMeasure.length,pathMeasure.length,leftPath,true)
        paint.color = Color.WHITE
        paint.alpha = paintAlpha
        canvas.drawPath(leftPath,paint)
        paint.color = Color.WHITE
        paint.alpha = 255
        canvas.drawPath(currentPath,paint)
    }
}