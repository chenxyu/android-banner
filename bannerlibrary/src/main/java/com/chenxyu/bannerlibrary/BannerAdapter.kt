package com.chenxyu.bannerlibrary

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/3/2 2:12
 * @Description:
 * @Version:       1.0
 */
class BannerAdapter(private val mContext: Context?, private val mImages: MutableList<Any>,
                    private val mPlaceholder: Int?, private val mError: Int?,
                    private val mScaleType: ImageView.ScaleType?,
                    private val requestOptions: RequestOptions?,
                    private val mOnItemClickListener: BannerView.OnItemClickListener?)
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
                    val options = RequestOptions()
                    mPlaceholder?.let { options.placeholder(it) }
                    mError?.let { options.error(it) }
                    Glide.with(mContext!!)
                            .load(any)
                            .apply(requestOptions ?: options)
                            .transition(withCrossFade())
                }
                is Int -> {
                    val requestOptions = RequestOptions()
                    mPlaceholder?.let { requestOptions.placeholder(it) }
                    mError?.let { requestOptions.error(it) }
                    (itemView as ImageView).setImageResource(any)
                }
            }
            itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(it, position)
            }
        }
    }
}