package com.chenxyu.bannerlibrary.indicator

import android.graphics.drawable.StateListDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.BannerView2
import com.chenxyu.bannerlibrary.R
import com.chenxyu.bannerlibrary.extend.az
import com.chenxyu.bannerlibrary.extend.dpToPx
import com.chenxyu.bannerlibrary.extend.getDrawable2

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/19 15:49
 * @Description:   Drawable指示器（支持XML设置）
 * @Version:       1.0
 * @param overlap           指示器是否重叠在Banner上
 * @param normalDrawable    默认Indicator
 * @param selectedDrawable  选中Indicator
 * @param margin            指示器外边距，间隔（DP）
 * @param gravity           指示器的位置
 */
class DrawableIndicator(overlap: Boolean? = true,
                        @DrawableRes normalDrawable: Int? = R.drawable.indicator_gray,
                        @DrawableRes selectedDrawable: Int? = R.drawable.indicator_white,
                        margin: Int? = 4,
                        gravity: Int? = Gravity.CENTER
) : Indicator() {
    /**
     * 指示器集
     */
    private var mIndicators: MutableList<View> = mutableListOf()

    /**
     * 指示器外边距，间隔（DP）
     */
    var indicatorMargin: Int = 4

    /**
     * 指示器宽（DP）
     */
    var indicatorWidth: Int = 7

    /**
     * 指示器高（DP）
     */
    var indicatorHeight: Int = 7

    /**
     * 选中时指示器宽（DP）
     */
    var indicatorSelectedW: Int = 7

    /**
     * 选中时指示器高（DP）
     */
    var indicatorSelectedH: Int = 7

    /**
     * 默认Indicator Drawable
     */
    @DrawableRes
    var indicatorNormalDrawable: Int = R.drawable.indicator_gray

    /**
     * 选中Indicator Drawable
     */
    @DrawableRes
    var indicatorSelectedDrawable: Int = R.drawable.indicator_white

    /**
     * 默认指示器LayoutParams
     */
    lateinit var normalParams: LinearLayout.LayoutParams

    /**
     * 选中的指示器LayoutParams
     */
    lateinit var selectedParams: LinearLayout.LayoutParams

    init {
        overlap?.let { this.overlap = it }
        normalDrawable?.let { indicatorNormalDrawable = it }
        selectedDrawable?.let { indicatorSelectedDrawable = it }
        margin?.let { indicatorMargin = it }
        gravity?.let { indicatorGravity = it }
    }

    override fun registerOnPageChangeCallback(viewPager2: ViewPager2?) {
        if (mVp2PageChangeCallback == null) {
            mVp2PageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    toggleIndicator(null, viewPager2)
                }
            }
            viewPager2?.registerOnPageChangeCallback(mVp2PageChangeCallback!!)
        }
    }

    override fun addOnScrollListener(recyclerView: RecyclerView?) {
        if (mRvScrollListener == null) {
            mRvScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        // 闲置
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            toggleIndicator(recyclerView, null)
                        }
                        // 拖拽中
                        RecyclerView.SCROLL_STATE_DRAGGING -> {

                        }
                        // 惯性滑动中
                        RecyclerView.SCROLL_STATE_SETTLING -> {
                            toggleIndicator(recyclerView, null)
                        }
                    }
                }
            }
            recyclerView?.addOnScrollListener(mRvScrollListener!!)
        }
    }

    override fun initIndicator(container: LinearLayout, count: Int, orientation: Int) {
        mIndicators.clear()
        normalParams = LinearLayout.LayoutParams(
                indicatorWidth.dpToPx(container.context),
                indicatorHeight.dpToPx(container.context))
        selectedParams = LinearLayout.LayoutParams(
                indicatorSelectedW.dpToPx(container.context),
                indicatorSelectedH.dpToPx(container.context))
        if (orientation == RecyclerView.HORIZONTAL) {
            normalParams.setMargins(indicatorMargin.dpToPx(container.context), 0,
                    indicatorMargin.dpToPx(container.context), 0)
            selectedParams.setMargins(indicatorMargin.dpToPx(container.context), 0,
                    indicatorMargin.dpToPx(container.context), 0)

        } else {
            normalParams.setMargins(0, indicatorMargin.dpToPx(container.context),
                    0, indicatorMargin.dpToPx(container.context))
            selectedParams.setMargins(0, indicatorMargin.dpToPx(container.context),
                    0, indicatorMargin.dpToPx(container.context))
        }
        repeat(count) {
            val indicators = View(container.context)
            val drawable = StateListDrawable()
            drawable.addState(IntArray(0).plus(android.R.attr.state_selected),
                    container.context.getDrawable2(indicatorSelectedDrawable))
            drawable.addState(IntArray(0), container.context.getDrawable2(indicatorNormalDrawable))
            indicators.background = drawable
            indicators.isSelected = false
            indicators.layoutParams = normalParams
            container.addView(indicators)
            mIndicators.add(indicators)
        }
        mIndicators[0].isSelected = true
        mIndicators[0].layoutParams = selectedParams
    }

    /**
     * 切换指示器位置
     */
    private fun toggleIndicator(recyclerView: RecyclerView?, viewPager2: ViewPager2?) {
        val currentPosition = viewPager2?.currentItem ?: recyclerView?.getChildAt(0)
                ?.layoutParams?.az<RecyclerView.LayoutParams>()?.viewAdapterPosition
        val itemCount = viewPager2?.adapter?.itemCount
                ?: recyclerView?.adapter?.az<BannerView2.Adapter<*, *>>()?.itemCount ?: return
        for (indicator in mIndicators) {
            indicator.isSelected = false
            indicator.layoutParams = normalParams
        }
        if (isLoop) {
            when (currentPosition) {
                0 -> {
                    mIndicators[mIndicators.size - 1].isSelected = true
                    mIndicators[mIndicators.size - 1].layoutParams = selectedParams
                    return
                }
                itemCount.minus(1) -> {
                    mIndicators[0].isSelected = true
                    mIndicators[0].layoutParams = selectedParams
                    return
                }
                itemCount.minus(2) -> {
                    mIndicators[mIndicators.size - 1].isSelected = true
                    mIndicators[mIndicators.size - 1].layoutParams = selectedParams
                    return
                }
            }
            for (i in mIndicators.indices) {
                if (currentPosition == i) {
                    mIndicators[i - 1].isSelected = true
                    mIndicators[i - 1].layoutParams = selectedParams
                    return
                }
            }
        } else {
            currentPosition?.let {
                mIndicators[it].isSelected = true
                mIndicators[it].layoutParams = selectedParams
            }
        }
    }
}