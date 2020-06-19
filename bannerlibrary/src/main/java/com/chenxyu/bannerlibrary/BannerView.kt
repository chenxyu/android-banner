package com.chenxyu.bannerlibrary

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.adapter.BaseBannerAdapter
import com.chenxyu.bannerlibrary.transformer.DepthPageTransformer
import com.chenxyu.bannerlibrary.transformer.RotationPageTransformer
import com.chenxyu.bannerlibrary.transformer.ScalePageTransformer
import com.chenxyu.bannerlibrary.transformer.ZoomOutPageTransformer
import java.lang.ref.WeakReference

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/3/2 0:38
 * @Description:
 * @Version:       1.0
 */
class BannerView : RelativeLayout {
    private var mContext: Context? = null
    private var mRootLayout: RelativeLayout? = null
    var mViewPager: ViewPager2? = null
    private var mBottomIndicatorLayout: LinearLayout? = null
    private var mEndIndicatorLayout: LinearLayout? = null
    private var mIndicators: MutableList<ImageView>? = null
    private var mIndicatorUnselected: Int? = null
    private var mIndicatorSelected: Int? = null
    private var mIndicatorWH = 20
    private var mIndicatorMargin = 8
    private var mDataSize: Int = 0
    private var mIndicatorSize: Int = 0
    private var mOffscreenPageLimit = 2
    private var isTouch = false
    private var mLifecycleOwner: LifecycleOwner? = null
    private var mDelayMillis: Long = 5000
    private var isLoopViews: Boolean = true
    private var mBaseBannerAdapter: BaseBannerAdapter<*, *>? = null
    private val mHandler: Handler = BannerHandler(this)

    companion object {
        const val HORIZONTAL = ViewPager2.ORIENTATION_HORIZONTAL
        const val VERTICAL = ViewPager2.ORIENTATION_VERTICAL
    }

    class BannerHandler(view: BannerView) : Handler() {
        private val weakReference = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val bannerView = weakReference.get()
            if (bannerView?.isTouch != null && !bannerView.isTouch) {
                bannerView.mViewPager?.let {
                    if (it.currentItem == it.childCount.minus(1)) {
                        it.setCurrentItem(1, false)
                    } else {
                        it.beginFakeDrag()
                        if (it.orientation == HORIZONTAL) {
                            if (it.fakeDragBy(-it.width.toFloat() / 2)) {
                                it.endFakeDrag()
                            }
                        } else {
                            if (it.fakeDragBy(-it.height.toFloat() / 2)) {
                                it.endFakeDrag()
                            }
                        }
                    }
                }
            }
            bannerView?.mDelayMillis?.let { sendEmptyMessageDelayed(0, it) }
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
        init()
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.BannerView)
        attributes?.let {
            mViewPager?.orientation = it.getInteger(R.styleable.BannerView_orientation, HORIZONTAL)
            it.getResourceId(R.styleable.BannerView_indicatorUnselected, -1).takeIf { resource ->
                resource != -1
            }?.apply { mIndicatorUnselected = this }
            it.getResourceId(R.styleable.BannerView_indicatorSelected, -1).takeIf { resource ->
                resource != -1
            }?.apply { mIndicatorSelected = this }
            it.getDimension(R.styleable.BannerView_indicatorWH, 0f).takeIf { dimension ->
                dimension > 0f
            }?.apply { mIndicatorWH = this.toInt() }
            it.getDimension(R.styleable.BannerView_indicatorMargin, 0f).takeIf { dimension ->
                dimension > 0f
            }?.apply { mIndicatorMargin = this.toInt() }
            mBottomIndicatorLayout?.gravity = it.getInteger(R.styleable.BannerView_indicatorGravity, Gravity.CENTER)
            mEndIndicatorLayout?.gravity = it.getInteger(R.styleable.BannerView_indicatorGravity, Gravity.CENTER)
            mBottomIndicatorLayout?.visibility = it.getInteger(R.styleable.BannerView_indicatorVisibility, View.VISIBLE)
            mEndIndicatorLayout?.visibility = it.getInteger(R.styleable.BannerView_indicatorVisibility, View.VISIBLE)
            isLoopViews = it.getBoolean(R.styleable.BannerView_loopViews, true)
            mOffscreenPageLimit = it.getInteger(R.styleable.BannerView_offscreenPageLimit, mOffscreenPageLimit)
        }
        attributes?.recycle()
    }

    private fun init() {
        val view = View.inflate(mContext, R.layout.banner_view, null)
        addView(view)
        mRootLayout = view.findViewById(R.id.root_layout)
        mViewPager = view.findViewById(R.id.view_pager)
        mBottomIndicatorLayout = view.findViewById(R.id.bottom_indicator_layout)
        mEndIndicatorLayout = view.findViewById(R.id.end_indicator_layout)
    }

    private fun initIndicator() {
        mIndicators = mutableListOf()
        repeat(mIndicatorSize) {
            val indicators = ImageView(mContext)
            indicators.setImageResource(mIndicatorUnselected ?: R.drawable.indicator_gray)
            val layoutParams = LayoutParams(mIndicatorWH, mIndicatorWH)
            if (mViewPager?.orientation == HORIZONTAL) {
                layoutParams.setMargins(mIndicatorMargin, 0, mIndicatorMargin, 0)
                indicators.layoutParams = layoutParams
                mBottomIndicatorLayout?.addView(indicators)
            } else {
                layoutParams.setMargins(0, mIndicatorMargin, 0, mIndicatorMargin)
                indicators.layoutParams = layoutParams
                mEndIndicatorLayout?.addView(indicators)
            }
            mIndicators?.add(indicators)
        }
        mIndicators?.get(0)?.setImageResource(mIndicatorSelected
                ?: R.drawable.indicator_white)
    }

    private fun initViewPager() {
        mViewPager?.let {
            it.offscreenPageLimit = mOffscreenPageLimit
            it.adapter = mBaseBannerAdapter
            it.currentItem = 1
            it.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            it.endFakeDrag()
                            isTouch = false
                            mHandler.removeMessages(0)
                            mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
                            if (it.currentItem == 0) {
                                it.setCurrentItem(mDataSize - 2, false)
                            }
                            if (it.currentItem == mDataSize - 1) {
                                it.setCurrentItem(1, false)
                            }
                        }
                        ViewPager2.SCROLL_STATE_DRAGGING -> {
                            isTouch = true
                        }
                        ViewPager2.SCROLL_STATE_SETTLING -> {
                            it.endFakeDrag()
                            isTouch = false
                            mHandler.removeMessages(0)
                            mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mIndicators?.let { indicators ->
                        for (indicator in indicators) {
                            indicator.setImageResource(mIndicatorUnselected
                                    ?: R.drawable.indicator_gray)
                        }
                        when (position) {
                            0 -> {
                                indicators[indicators.size - 1].setImageResource(mIndicatorSelected
                                        ?: R.drawable.indicator_white)
                                return
                            }
                            mDataSize - 1 -> {
                                indicators[0].setImageResource(mIndicatorSelected
                                        ?: R.drawable.indicator_white)
                                return
                            }
                            mDataSize - 2 -> {
                                indicators[indicators.size - 1].setImageResource(mIndicatorSelected
                                        ?: R.drawable.indicator_white)
                                return
                            }
                        }
                        for (i in indicators.indices) {
                            if (position == i) {
                                indicators[i - 1].setImageResource(mIndicatorSelected
                                        ?: R.drawable.indicator_white)
                                return
                            }
                        }
                    }
                }
            })

            mLifecycleOwner?.lifecycle?.addObserver(LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
                    Lifecycle.Event.ON_PAUSE -> mHandler.removeMessages(0)
                    Lifecycle.Event.ON_DESTROY -> {
                        mHandler.removeMessages(0)
                        mBaseBannerAdapter?.getData()?.clear()
                        mDataSize = 0
                        mIndicatorSize = 0
                    }
                    else -> {
                    }
                }
            })

            if (isLoopViews) {
                mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
            }
        }
    }

    /**
     * 观察Fragment或Activity生命周期控制Banner开始和暂停
     * @param lifecycleOwner Fragment or Activity
     */
    fun setLifecycle(lifecycleOwner: LifecycleOwner): BannerView {
        this.mLifecycleOwner = lifecycleOwner
        return this
    }

    /**
     * 自定义Adapter（继承BaseBannerAdapter）
     * @param baseBannerAdapter 自定义Adapter
     */
    fun setAdapter(baseBannerAdapter: BaseBannerAdapter<*, *>): BannerView {
        this.mBaseBannerAdapter = baseBannerAdapter.apply {
            mIndicatorSize = getRealItemCount()
            mDataSize = itemCount
        }
        return this
    }

    /**
     * 是否循环
     */
    fun isLoopViews(loop: Boolean): BannerView {
        this.isLoopViews = loop
        return this
    }

    /**
     * 预加载页面限制（默认2）
     */
    fun setOffscreenPageLimit(@ViewPager2.OffscreenPageLimit limit: Int) {
        this.mOffscreenPageLimit = limit
    }

    /**
     * 未选中指示器DrawableRes
     */
    fun setIndicatorUnselected(@DrawableRes indicatorUnselected: Int?): BannerView {
        this.mIndicatorUnselected = indicatorUnselected
        return this
    }

    /**
     * 选中指示器DrawableRes
     */
    fun setIndicatorSelected(@DrawableRes indicatorSelected: Int?): BannerView {
        this.mIndicatorSelected = indicatorSelected
        return this
    }

    /**
     * 指示器宽高
     */
    fun setIndicatorWH(indicatorWH: Int): BannerView {
        this.mIndicatorWH = indicatorWH
        return this
    }

    /**
     * 指示器Margin
     */
    fun setIndicatorMargin(indicatorMargin: Int): BannerView {
        this.mIndicatorMargin = indicatorMargin
        return this
    }

    /**
     * 页面切换时间
     */
    fun setDelayMillis(delayMillis: Long): BannerView {
        this.mDelayMillis = delayMillis
        return this
    }

    /**
     * 指示器显示或隐藏
     * @param visibility View.GONE or View.VISIBLE or View.INVISIBLE
     */
    fun setIndicatorVisibility(visibility: Int): BannerView {
        mBottomIndicatorLayout?.visibility = visibility
        mEndIndicatorLayout?.visibility = visibility
        return this
    }

    /**
     * 设置指示器位置
     * 默认中
     * @param gravity 位置[Gravity]
     */
    fun setIndicatorGravity(gravity: Int): BannerView {
        mBottomIndicatorLayout?.gravity = gravity
        mEndIndicatorLayout?.gravity = gravity
        return this
    }

    /**
     * 设置页面间距
     * @param marginPx 间距Px
     */
    fun setPageMargin(marginPx: Int): BannerView {
        mViewPager?.setPageTransformer(MarginPageTransformer(marginPx))
        return this
    }

    /**
     * 滑动方向
     * @param orientation [HORIZONTAL][VERTICAL]
     */
    fun setOrientation(orientation: Int): BannerView {
        mViewPager?.orientation = orientation
        return this
    }

    /**
     * 一屏多页，在[setOrientation]之后设置
     * @param marginPx 间距Px
     */
    fun setMultiPage(marginPx: Int): BannerView {
        mRootLayout?.clipChildren = false
        mViewPager?.clipChildren = false
        val params = mViewPager?.layoutParams as MarginLayoutParams
        if (mViewPager?.orientation == HORIZONTAL) {
            params.leftMargin = marginPx * 2
            params.rightMargin = params.leftMargin
        } else {
            params.topMargin = marginPx * 2
            params.bottomMargin = params.topMargin
        }
        return this
    }

    /**
     * 缩放动画，在[setOrientation]之后设置
     */
    fun setScalePageTransformer(): BannerView {
        mViewPager?.let { setPageTransformer(ScalePageTransformer(it.orientation)) }
        return this
    }

    /**
     * 官方示例缩放动画，在[setOrientation]之后设置
     */
    fun setZoomOutPageTransformer(): BannerView {
        mViewPager?.let { setPageTransformer(ZoomOutPageTransformer(it.orientation)) }
        return this
    }

    /**
     * 官方示例旋转动画，在[setOrientation]之后设置
     */
    fun setRotationPageTransformer(): BannerView {
        mViewPager?.let { setPageTransformer(RotationPageTransformer(it.orientation)) }
        return this
    }

    /**
     * 官方示例深度动画，在[setOrientation]之后设置
     */
    fun setDepthPageTransformer(): BannerView {
        mViewPager?.let { setPageTransformer(DepthPageTransformer(it.orientation)) }
        return this
    }

    /**
     * 自定义动画
     */
    fun setPageTransformer(transformer: ViewPager2.PageTransformer): BannerView {
        mViewPager?.setPageTransformer(transformer)
        return this
    }

    /**
     * 开始构建Banner
     */
    fun build() {
        if (mBaseBannerAdapter == null) throw NullPointerException(
                "${BannerView::class.qualifiedName}.setAdapter()")

        if (mViewPager?.orientation == HORIZONTAL) {
            mBottomIndicatorLayout?.visibility = View.VISIBLE
            mEndIndicatorLayout?.visibility = View.GONE
        } else {
            mBottomIndicatorLayout?.visibility = View.GONE
            mEndIndicatorLayout?.visibility = View.VISIBLE
        }

        initIndicator()
        initViewPager()
    }

    /**
     * 开始循环
     */
    fun start() {
        if (mDataSize > 0) {
            mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
        }
    }

    /**
     * 暂停循环
     */
    fun pause() {
        if (mDataSize > 0) {
            mHandler.removeMessages(0)
        }
    }

}
