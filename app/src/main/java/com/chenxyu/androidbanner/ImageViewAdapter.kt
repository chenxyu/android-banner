package com.chenxyu.androidbanner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.chenxyu.bannerlibrary.adapter.BaseBannerAdapter

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/6/19 18:12
 * @Description:
 * @Version:       1.0
 */
class ImageViewAdapter(private val mContext: Context?, mImages: MutableList<String?>)
    : BaseBannerAdapter<ImageViewAdapter.ImageViewHolder, String>(mImages) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(mContext)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = layoutParams
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int, item: String?) {
        holder.initView(mContext, item)
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun initView(mContext: Context?, item: String?) {
            mContext?.let {
                Glide.with(it)
                        .load(item)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .transition(withCrossFade())
                        .into(itemView as ImageView)
            }
        }
    }
}