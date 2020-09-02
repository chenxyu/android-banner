package com.chenxyu.bannerlibrary

import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.chenxyu.bannerlibrary.extend.dpToPx

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/19 13:19
 * @Description:   自定义指示器需继承此类
 * @Version:       1.0
 * @param indicatorMargin 指示器边距（DP）
 * @param indicatorWidth 指示器宽（DP）
 * @param indicatorHeight 指示器高（DP）
 * @param indicatorLayoutWH 指示器布局的宽和高（DP）
 */
abstract class Indicator(
        var indicatorMargin: Int = 4, var indicatorWidth: Int = 8,
        var indicatorHeight: Int = 8, var indicatorLayoutWH: Int = 20
) {
    /**
     * 指示器集
     */
    private var mIndicators: MutableList<ImageView> = mutableListOf()

    /**
     * RecyclerView滑动监听
     */
    private var mRvScrollListener: RecyclerView.OnScrollListener? = null

    /**
     * 默认Indicator Drawable
     */
    @DrawableRes
    abstract fun getNormalIndicatorDrawable(): Int

    /**
     * 选中Indicator Drawable
     */
    @DrawableRes
    abstract fun getSelectedIndicatorDrawable(): Int

    /**
     * 添加OnScrollListener
     */
    fun addOnScrollListener(recyclerView: RecyclerView?) {
        if (mRvScrollListener == null) {
            mRvScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        // 闲置
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            toggleIndicator(recyclerView)
                        }
                        // 拖拽中
                        RecyclerView.SCROLL_STATE_DRAGGING -> {

                        }
                        // 惯性滑动中
                        RecyclerView.SCROLL_STATE_SETTLING -> {

                        }
                    }
                }
            }
            recyclerView?.addOnScrollListener(mRvScrollListener!!)
        }
    }

    /**
     * 删除RecyclerView
     */
    fun removeRecyclerView(recyclerView: RecyclerView?) {
        mRvScrollListener?.let {
            recyclerView?.removeOnScrollListener(it)
        }
    }

    /**
     * 切换指示器位置
     */
    private fun toggleIndicator(recyclerView: RecyclerView?) {
        val currentPosition = (recyclerView?.getChildAt(0)
                ?.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val adapter = recyclerView.adapter as BannerView2.Adapter<*, *>
        for (indicator in mIndicators) {
            indicator.setImageResource(getNormalIndicatorDrawable())
        }
        when (currentPosition) {
            0 -> {
                mIndicators[mIndicators.size - 1].setImageResource(getSelectedIndicatorDrawable())
                return
            }
            adapter.itemCount - 1 -> {
                mIndicators[0].setImageResource(getSelectedIndicatorDrawable())
                return
            }
            adapter.itemCount - 2 -> {
                mIndicators[mIndicators.size - 1].setImageResource(getSelectedIndicatorDrawable())
                return
            }
        }
        for (i in mIndicators.indices) {
            if (currentPosition == i) {
                mIndicators[i - 1].setImageResource(getSelectedIndicatorDrawable())
                return
            }
        }
    }

    /**
     * 设置指示器
     * @param relativeLayout BannerView2
     * @param count 数量
     * @param orientation 方向
     */
    fun setIndicator(relativeLayout: RelativeLayout, count: Int, orientation: Int) {
        mIndicators.clear()
        val indicatorLayout = LinearLayout(relativeLayout.context).apply {
            this.orientation = orientation
            this.gravity = Gravity.CENTER
        }

        repeat(count) {
            val indicators = ImageView(relativeLayout.context)
            indicators.setImageResource(getNormalIndicatorDrawable())
            val layoutParams = RelativeLayout.LayoutParams(
                    indicatorWidth.dpToPx(relativeLayout.context),
                    indicatorHeight.dpToPx(relativeLayout.context))
            if (orientation == BannerView2.HORIZONTAL) {
                layoutParams.setMargins(indicatorMargin.dpToPx(relativeLayout.context), 0,
                        indicatorMargin.dpToPx(relativeLayout.context), 0)
                indicators.layoutParams = layoutParams
                indicatorLayout.addView(indicators)
            } else {
                layoutParams.setMargins(0, indicatorMargin.dpToPx(relativeLayout.context),
                        0, indicatorMargin.dpToPx(relativeLayout.context))
                indicators.layoutParams = layoutParams
                indicatorLayout.addView(indicators)
            }
            mIndicators.add(indicators)
        }
        mIndicators[0].setImageResource(getSelectedIndicatorDrawable())

        when (orientation) {
            BannerView2.HORIZONTAL -> {
                val layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        indicatorLayoutWH.dpToPx(relativeLayout.context))
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                relativeLayout.addView(indicatorLayout, layoutParams)
            }
            BannerView2.VERTICAL -> {
                val layoutParams = RelativeLayout.LayoutParams(
                        indicatorLayoutWH.dpToPx(relativeLayout.context),
                        RelativeLayout.LayoutParams.MATCH_PARENT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                relativeLayout.addView(indicatorLayout, layoutParams)
            }
        }
    }
}