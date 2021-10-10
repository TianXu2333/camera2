package com.tx.openglcamera.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * create by xu.tian
 * 主要用来实现不可滑动，只能单击的列表
 * @date 2021/10/10
 */
class SingleChoiceRecyclerView(context: Context, attrs: AttributeSet?) :
    RecyclerView(context, attrs) {

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return true
    }

}