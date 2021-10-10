package com.tx.openglcamera.view

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.ScaleAnimation
import android.widget.PopupWindow
import androidx.recyclerview.widget.GridLayoutManager
import com.tx.openglcamera.R
import com.tx.openglcamera.camera.CameraProvider
import com.tx.openglcamera.databinding.LayoutTopSettingPanelBinding
import com.tx.openglcamera.view.preview_size.PreviewSizeAdapter
import com.tx.openglcamera.view.preview_size.PreviewSizeItem
import kotlin.Array as Array1

/**
 * create by xu.tian
 * @date 2021/10/10
 */
class SettingPanel(var context: Context? , var cameraProvider: CameraProvider) : PopupWindow(context) {
    var binding : LayoutTopSettingPanelBinding = LayoutTopSettingPanelBinding.inflate(LayoutInflater.from(context))
    lateinit var previewAdapter : PreviewSizeAdapter
    init {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        contentView = binding.root
        animationStyle = R.style.setting_anim_style
        isOutsideTouchable = true
        binding.cancelButton.setOnClickListener{
            dismiss()
        }
        previewAdapter = PreviewSizeAdapter(getPreviewSizeList())
        binding.previewSizeRv.layoutManager = GridLayoutManager(context,3)
        binding.previewSizeRv.adapter = previewAdapter
        previewAdapter.listener = object : PreviewSizeAdapter.OnItemClickListener{
            override fun onClick(size: Size) {
                cameraProvider.changePreviewSize(size.width,size.height)
                previewAdapter.data = getPreviewSizeList()
                previewAdapter.notifyDataSetChanged()
            }

        }

    }

    private fun getPreviewSizeList(): ArrayList<PreviewSizeItem> {
        var sizeArr = cameraProvider.getPreviewSize()
        var array = ArrayList<PreviewSizeItem>()
        if (sizeArr != null) {
            for (size in sizeArr){
                var isChecked = false
                if (size.width==cameraProvider.previewWidth && size.height==cameraProvider.previewHeight){
                    isChecked = true
                }
                var previewSizeItem = PreviewSizeItem(size,isChecked)
                array.add(previewSizeItem)
            }
        }
        return array
    }


}