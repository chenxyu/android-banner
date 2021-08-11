package com.chenxyu.bannerlibrary.indicator

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.extend.dpToPx

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/19 13:19
 * @Description:   自定义指示器需继承此类
 * @Version:       1.0
 */
abstract class Indicator {
    /**
     * 指示器是否重叠在Banner上
     * 最下面或最右边根据方向决定
     */
    var overlap: Boolean = true

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
     * ViewPager2页面变化监听
     */
    var mVp2PageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    /**
     * RecyclerView滑动监听
     */
    var mRvScrollListener: RecyclerView.OnScrollListener? = null

    /**
     * 是否循环
     */
    var isLoop: Boolean = true

    /**
     * 添加OnPageChangeCallback
     */
    abstract fun registerOnPageChangeCallback(viewPager2: ViewPager2?)

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
    abstract fun addOnScrollListener(recyclerView: RecyclerView?)

    /**
     * 删除OnScrollListener
     */
    fun removeScrollListener(recyclerView: RecyclerView?) {
        mRvScrollListener?.let {
            recyclerView?.removeOnScrollListener(it)
        }
    }

    /**
     * 设置指示器
     * @param relativeLayout BannerView or BannerView2
     * @param count 真实数量
     * @param orientation 方向
     * @param isLoop 是否循环
     */
    fun initialize(relativeLayout: RelativeLayout, count: Int, orientation: Int, isLoop: Boolean = true) {
        this.isLoop = isLoop
        if (relativeLayout.childCount == 2) relativeLayout.removeViewAt(1)
        if (relativeLayout.childCount > 2) throw RuntimeException("There can only be one child view")
        val indicatorLayout = LinearLayout(relativeLayout.context).apply {
            this.orientation = orientation
            this.gravity = indicatorGravity
        }
        initIndicator(indicatorLayout, count, orientation)
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

    /**
     * 设置指示器
     * @param container 指示器ViewGroup
     * @param count 真实数量
     * @param orientation 方向
     */
    abstract fun initIndicator(container: LinearLayout, count: Int, orientation: Int)
}