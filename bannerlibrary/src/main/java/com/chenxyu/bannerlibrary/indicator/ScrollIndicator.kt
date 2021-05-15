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
 * @CreateDate:    2021/3/27 19:40
 * @Description:   滚动条指示器（仅BannerView2有效）
 * @Version:       1.0
 * @param overlap  指示器是否重叠在Banner上
 */
class ScrollIndicator(overlap: Boolean = true) : Indicator() {
    private var mScrollBar: ScrollBar? = null
    private var mOrientation: Int = RecyclerView.HORIZONTAL
    private var maxWH: Int = 0

    /**
     * 滚动条颜色
     */
    @ColorInt
    var indicatorColor: Int? = null

    /**
     * 滚动条轨道颜色
     */
    @ColorInt
    var indicatorTrackColor: Int? = null

    /**
     * 指示器宽（DP）
     */
    var indicatorW: Int = 15

    /**
     * 指示器高（DP）
     */
    var indicatorH: Int = 15

    /**
     * 是否设置GridLayoutManager
     */
    var isGrid: Boolean = false

    /**
     * GridLayoutManager行或列显示个数
     */
    var spanCount: Int = 1

    init {
        this.overlap = overlap
    }

    override fun registerOnPageChangeCallback(viewPager2: ViewPager2?) {

    }

    override fun addOnScrollListener(recyclerView: RecyclerView?) {
        if (mRvScrollListener == null) {
            mRvScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.childCount > 1 && maxWH == 0) {
                        recyclerView.adapter?.az<BannerView2.Adapter<*, *>>()?.let { adapter ->
                            when (mOrientation) {
                                RecyclerView.HORIZONTAL -> {
                                    val itemCount: Int = if (adapter.itemCount.minus(adapter.showCount) > 0) {
                                        if (isGrid) {
                                            val count = adapter.itemCount.minus(adapter.showCount * spanCount)
                                            when (count % spanCount) {
                                                0 -> count.div(spanCount)
                                                1 -> count.div(spanCount) + 1
                                                else -> 1
                                            }
                                        } else {
                                            adapter.itemCount.minus(adapter.showCount)
                                        }
                                    } else {
                                        0
                                    }
                                    val childView = recyclerView.getChildAt(0)
                                    if (itemCount > 0) {
                                        maxWH = itemCount.times(childView?.width ?: 0)
                                    }
                                    childView?.layoutParams?.az<RecyclerView.LayoutParams>()?.let {
                                        maxWH += if (isGrid) {
                                            (it.marginStart + it.marginEnd).times(when (adapter.itemCount % spanCount) {
                                                0 -> adapter.itemCount.div(spanCount)
                                                1 -> adapter.itemCount.div(spanCount) + 1
                                                else -> adapter.itemCount.div(spanCount) + 1
                                            })
                                        } else {
                                            (it.marginStart + it.marginEnd).times(adapter.itemCount)
                                        }
                                    }
                                }
                                RecyclerView.VERTICAL -> {
                                    val itemCount: Int = if (adapter.itemCount.minus(adapter.showCount) > 0) {
                                        if (isGrid) {
                                            val count = adapter.itemCount.minus(adapter.showCount * spanCount)
                                            when (count % spanCount) {
                                                0 -> count.div(spanCount)
                                                1 -> count.div(spanCount) + 1
                                                else -> 1
                                            }
                                        } else {
                                            adapter.itemCount.minus(adapter.showCount)
                                        }
                                    } else {
                                        0
                                    }
                                    val childView = recyclerView.getChildAt(0)
                                    if (itemCount > 0) {
                                        maxWH = itemCount.times(childView?.height ?: 0)
                                    }
                                    childView?.layoutParams?.az<RecyclerView.LayoutParams>()?.let {
                                        maxWH += if (isGrid) {
                                            (it.topMargin + it.bottomMargin).times(when (adapter.itemCount % spanCount) {
                                                0 -> adapter.itemCount.div(spanCount)
                                                1 -> adapter.itemCount.div(spanCount) + 1
                                                else -> adapter.itemCount.div(spanCount) + 1
                                            })
                                        } else {
                                            (it.topMargin + it.bottomMargin).times(adapter.itemCount)
                                        }
                                    }
                                }
                                else -> {
                                }
                            }
                        }
                    }
                    mScrollBar?.scroll(maxWH, dx, dy)
                }
            }
            recyclerView?.addOnScrollListener(mRvScrollListener!!)
        }
    }

    override fun initIndicator(container: LinearLayout, count: Int, orientation: Int) {
        mOrientation = orientation
        val displayMetrics = container.context.resources.displayMetrics
        mScrollBar = ScrollBar(container.context).apply {
            this.orientation = orientation
        }
        indicatorColor?.let {
            mScrollBar?.barColor = it
        }
        indicatorTrackColor?.let {
            mScrollBar?.trackColor = it
        }
        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                indicatorW = if (indicatorW == 15) {
                    displayMetrics?.widthPixels?.div(12) ?: 0
                } else {
                    indicatorW.dpToPx(container.context)
                }
            }
            RecyclerView.VERTICAL -> {
                indicatorH = if (indicatorH == 15) {
                    displayMetrics?.heightPixels?.div(15) ?: 0
                } else {
                    indicatorH.dpToPx(container.context)
                }
            }
        }
        mScrollBar?.layoutParams = LinearLayout.LayoutParams(indicatorW, indicatorH)
        container.addView(mScrollBar)
    }

}