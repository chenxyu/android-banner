package com.chenxyu.bannerlibrary

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/3/2 2:12
 * @Description:
 * @Version:       1.0
 */
class BannerAdapter(private val mContext: Context?, private val mImages: MutableList<Any>,
                    private var mPlaceholder: Int?, private var mError: Int?,
                    private val mScaleType: ImageView.ScaleType?,
                    private var mOnItemClickListener: BannerView.OnItemClickListener?)
    : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val imageView = ImageView(mContext)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        imageView.layoutParams = layoutParams
        if (mScaleType != null) imageView.scaleType = mScaleType
        return BannerViewHolder(imageView)
    }

    override fun getItemCount(): Int = mImages.size

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bindView(mImages[position], position)
    }

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(any: Any, position: Int) {
            when (any) {
                is String -> {
                    val requestOptions = RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    mPlaceholder?.let { requestOptions.placeholder(it) }
                    mError?.let { requestOptions.error(it) }
                    Glide.with(mContext!!)
                            .load(any)
                            .apply(requestOptions)
                            .transition(withCrossFade())
                            .into(itemView as ImageView)
                }
                is Int -> {
                    val requestOptions = RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    mPlaceholder?.let { requestOptions.placeholder(it) }
                    mError?.let { requestOptions.error(it) }
                    (itemView as ImageView).setImageResource(any)
                }
            }
            itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(position)
            }
        }
    }
}