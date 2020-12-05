package com.chenxyu.androidbanner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.chenxyu.bannerlibrary.BannerView

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/6/19 18:12
 * @Description:
 * @Version:       1.0
 */
class ImageViewAdapter(private val mContext: Context?, mImages: MutableList<String?>)
    : BannerView.Adapter<ImageViewAdapter.ImageViewHolder, String>(mImages) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(mContext)
        val layoutParams = RelativeLayout.LayoutParams(350, 300)
        imageView.layoutParams = layoutParams
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int, item: String?) {
        holder.initView(item, position, mContext)
    }

    class ImageViewHolder(itemView: View) : BannerView.ViewHolder<String>(itemView) {

        override fun initView(item: String?, position: Int?, context: Context?) {
            context?.let {
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