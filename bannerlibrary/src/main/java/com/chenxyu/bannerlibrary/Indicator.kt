package com.chenxyu.bannerlibrary

import android.graphics.drawable.StateListDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.extend.dpToPx
import com.chenxyu.bannerlibrary.extend.getDrawable2

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/19 13:19
 * @Description:   自定义指示器需继承此类
 * @Version:       1.0
 */
abstract class Indicator {
    /**
     * 指示器在View最下面或最右边（不重叠在View上）
     * 最下面或最右边根据方向决定
     */
    var overlap: Boolean = true

    /**
     * 指示器外边距（DP）
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
     * 指示器布局的宽和高（DP）
     * 宽和高根据方向决定
     */
    var indicatorLayoutWH: Int = 20

    /**
     * 指示器的位置
     */
    var indicatorGravity: Int = Gravity.CENTER

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
     * 指示器集
     */
    private var mIndicators: MutableList<View> = mutableListOf()

    /**
     * ViewPager2页面变化监听
     */
    private var mVp2PageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    /**
     * RecyclerView滑动监听
     */
    private var mRvScrollListener: RecyclerView.OnScrollListener? = null

    /**
     * 默认指示器LayoutParams
     */
    private lateinit var normalParams: LinearLayout.LayoutParams

    /**
     * 选中的指示器LayoutParams
     */
    private lateinit var selectedParams: LinearLayout.LayoutParams

    /**
     * 是否循环
     */
    private var isLoop: Boolean = true

    /**
     * 添加OnPageChangeCallback
     */
    fun registerOnPageChangeCallback(viewPager2: ViewPager2?) {
        if (mVp2PageChangeCallback == null) {
            mVp2PageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    toggleIndicator(null, viewPager2)
                }
            }
            viewPager2?.registerOnPageChangeCallback(mVp2PageChangeCallback!!)
        }
    }

    /**
     * 删除OnPageChangeCallback
     */
    fun unregisterOnPageChangeCallback(viewPager2: ViewPager2?) {
        mVp2PageChangeCallback?.let {
            viewPager2?.unregisterOnPageChangeCallback(it)
        }
    }

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

    /**
     * 删除OnScrollListener
     */
    fun removeScrollListener(recyclerView: RecyclerView?) {
        mRvScrollListener?.let {
            recyclerView?.removeOnScrollListener(it)
        }
    }

    /**
     * 切换指示器位置
     */
    @Synchronized
    private fun toggleIndicator(recyclerView: RecyclerView?, viewPager2: ViewPager2?) {
        val currentPosition = viewPager2?.currentItem ?: (recyclerView?.getChildAt(0)
                ?.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        val itemCount = viewPager2?.adapter?.itemCount
                ?: (recyclerView?.adapter as BannerView2.Adapter<*, *>).itemCount
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
                itemCount - 1 -> {
                    mIndicators[0].isSelected = true
                    mIndicators[0].layoutParams = selectedParams
                    return
                }
                itemCount - 2 -> {
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
            mIndicators[currentPosition].isSelected = true
            mIndicators[currentPosition].layoutParams = selectedParams
        }
    }

    /**
     * 设置指示器
     * @param relativeLayout BannerView2
     * @param count 数量
     * @param orientation 方向
     * @param isLoop 是否循环
     */
    fun setIndicator(relativeLayout: RelativeLayout, count: Int, orientation: Int, isLoop: Boolean = true) {
        this.isLoop = isLoop
        if (relativeLayout.childCount == 2) relativeLayout.removeViewAt(1)
        if (relativeLayout.childCount > 2) throw RuntimeException("There can only be one child view")
        mIndicators.clear()
        val indicatorLayout = LinearLayout(relativeLayout.context).apply {
            this.orientation = orientation
            this.gravity = indicatorGravity
        }

        repeat(count) {
            val indicators = View(relativeLayout.context)
            val drawable = StateListDrawable()
            drawable.addState(IntArray(0).plus(android.R.attr.state_selected),
                    relativeLayout.context.getDrawable2(indicatorSelectedDrawable))
            drawable.addState(IntArray(0), relativeLayout.context.getDrawable2(indicatorNormalDrawable))
            indicators.background = drawable
            indicators.isSelected = false
            normalParams = LinearLayout.LayoutParams(
                    indicatorWidth.dpToPx(relativeLayout.context),
                    indicatorHeight.dpToPx(relativeLayout.context))
            selectedParams = LinearLayout.LayoutParams(
                    indicatorSelectedW.dpToPx(relativeLayout.context),
                    indicatorSelectedH.dpToPx(relativeLayout.context))
            if (orientation == RecyclerView.HORIZONTAL) {
                normalParams.setMargins(indicatorMargin.dpToPx(relativeLayout.context), 0,
                        indicatorMargin.dpToPx(relativeLayout.context), 0)
                selectedParams.setMargins(indicatorMargin.dpToPx(relativeLayout.context), 0,
                        indicatorMargin.dpToPx(relativeLayout.context), 0)
                indicators.layoutParams = normalParams
                indicatorLayout.addView(indicators)
            } else {
                normalParams.setMargins(0, indicatorMargin.dpToPx(relativeLayout.context),
                        0, indicatorMargin.dpToPx(relativeLayout.context))
                selectedParams.setMargins(0, indicatorMargin.dpToPx(relativeLayout.context),
                        0, indicatorMargin.dpToPx(relativeLayout.context))
                indicators.layoutParams = normalParams
                indicatorLayout.addView(indicators)
            }
            mIndicators.add(indicators)
        }
        mIndicators[0].isSelected = true
        mIndicators[0].layoutParams = selectedParams

        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                val layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        indicatorLayoutWH.dpToPx(relativeLayout.context))
                if (overlap) {
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                } else {
                    val h = if (relativeLayout.layoutParams.height == -1) {
                        relativeLayout.context.resources.displayMetrics.heightPixels
                    } else {
                        relativeLayout.layoutParams.height
                    }
                    relativeLayout.getChildAt(0).layoutParams.height = h - indicatorLayoutWH.dpToPx(relativeLayout.context)
                    layoutParams.addRule(RelativeLayout.BELOW, relativeLayout.getChildAt(0).id)
                }
                relativeLayout.addView(indicatorLayout, layoutParams)
            }
            RecyclerView.VERTICAL -> {
                val layoutParams = RelativeLayout.LayoutParams(
                        indicatorLayoutWH.dpToPx(relativeLayout.context),
                        RelativeLayout.LayoutParams.MATCH_PARENT)
                if (overlap) {
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                } else {
                    val w = if (relativeLayout.layoutParams.width == -1) {
                        relativeLayout.context.resources.displayMetrics.widthPixels
                    } else {
                        relativeLayout.layoutParams.width
                    }
                    relativeLayout.getChildAt(0).layoutParams.width = w - indicatorLayoutWH.dpToPx(relativeLayout.context)
                    layoutParams.addRule(RelativeLayout.END_OF, relativeLayout.getChildAt(0).id)
                }
                relativeLayout.addView(indicatorLayout, layoutParams)
            }
        }
    }
}