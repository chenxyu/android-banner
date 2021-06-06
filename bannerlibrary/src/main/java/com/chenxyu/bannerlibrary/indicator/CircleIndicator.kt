package com.chenxyu.bannerlibrary.indicator

import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.BannerView2
import com.chenxyu.bannerlibrary.extend.az
import com.chenxyu.bannerlibrary.extend.dpToPx

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2021/5/16 16:57
 * @Description:   圆形指示器
 * @Version:       1.0
 * @param overlap  指示器是否重叠在Banner上
 */
class CircleIndicator(overlap: Boolean = true) : Indicator() {
    private var mCircleView: CircleView? = null
    private var mOrientation: Int = RecyclerView.HORIZONTAL
    private var mCircleCount: Int = 0

    /**
     * 选中颜色
     */
    @ColorInt
    var selectedColor: Int? = null

    /**
     * 默认颜色
     */
    @ColorInt
    var normalColor: Int? = null

    /**
     * 圆间隔（DP）
     */
    var indicatorSpacing: Int = 10

    /**
     * 圆宽（DP）
     */
    var circleWidth: Float = 7F

    /**
     * 圆高（DP）
     */
    var circleHeight: Float = 7F

    init {
        this.overlap = overlap
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
//                            toggleIndicator(recyclerView, null)
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
        mOrientation = orientation
        mCircleCount = count
        mCircleView = CircleView(container.context).apply {
            this.orientation = orientation
        }
        selectedColor?.let {
            mCircleView?.selectedColor = it
        }
        normalColor?.let {
            mCircleView?.normalColor = it
        }
        mCircleView?.circleCount = mCircleCount
        mCircleView?.spacing = indicatorSpacing
        mCircleView?.circleWidth = circleWidth
        mCircleView?.circleHeight = circleHeight
        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                val width = circleWidth.times(mCircleCount).plus(
                        indicatorSpacing.times(mCircleCount))
                mCircleView?.layoutParams = LinearLayout.LayoutParams(
                        width.toInt().dpToPx(container.context),
                        circleHeight.toInt().dpToPx(container.context)
                )
            }
            RecyclerView.VERTICAL -> {
                val height = circleHeight.times(mCircleCount).plus(
                        indicatorSpacing.times(mCircleCount))
                mCircleView?.layoutParams = LinearLayout.LayoutParams(
                        circleWidth.toInt().dpToPx(container.context),
                        height.toInt().dpToPx(container.context)
                )
            }
        }
        container.addView(mCircleView)
    }

    /**
     * 切换指示器位置
     */
    private fun toggleIndicator(recyclerView: RecyclerView?, viewPager2: ViewPager2?) {
        val currentPosition = viewPager2?.currentItem ?: recyclerView?.getChildAt(0)
                ?.layoutParams?.az<RecyclerView.LayoutParams>()?.viewAdapterPosition
        val itemCount = viewPager2?.adapter?.itemCount
                ?: recyclerView?.adapter?.az<BannerView2.Adapter<*, *>>()?.itemCount ?: return
        if (isLoop) {
            when (currentPosition) {
                0 -> {
                    mCircleView?.scrollTo(mCircleCount - 1)
                    return
                }
                itemCount.minus(1) -> {
                    mCircleView?.scrollTo(0)
                    return
                }
                itemCount.minus(2) -> {
                    mCircleView?.scrollTo(mCircleCount - 1)
                    return
                }
            }
            repeat(mCircleCount) { i ->
                if (currentPosition == i) {
                    mCircleView?.scrollTo(i - 1)
                    return
                }
            }
        } else {
            currentPosition?.let {
                mCircleView?.scrollTo(it)
            }
        }
    }

}