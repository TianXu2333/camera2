package com.tx.openglcamera.view.preview_size

import android.graphics.Color
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tx.openglcamera.databinding.LayoutPreviewSizeItemBinding

/**
 * create by xu.tian
 * @date 2021/10/10
 */
class PreviewSizeAdapter(var data : ArrayList<PreviewSizeItem>) : RecyclerView.Adapter<PreviewSizeViewHolder>() {

    interface OnItemClickListener{
        fun onClick(size : Size)
    }

    var listener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewSizeViewHolder {
        var binding:LayoutPreviewSizeItemBinding = LayoutPreviewSizeItemBinding.inflate(
            LayoutInflater.from(parent.context))
        return PreviewSizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreviewSizeViewHolder, position: Int) {
        var item = data[position]
        var previewStr = "${item.size.width}x${item.size.height}"
        holder.binding.root.text = previewStr
        if (!item.isChecked){
            holder.binding.root.setTextColor(Color.WHITE)
        }else{
            holder.binding.root.setTextColor(Color.YELLOW)
        }
        holder.binding.root.setOnClickListener{
            if (listener!=null){
                listener!!.onClick(item.size)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}