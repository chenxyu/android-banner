package com.chenxyu.bannerlibrary

import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.extend.dpToPx
import com.chenxyu.bannerlibrary.listener.OnItemClickListener
import com.chenxyu.bannerlibrary.listener.OnItemLongClickListener
import com.chenxyu.bannerlibrary.transformer.DepthPageTransformer
import com.chenxyu.bannerlibrary.transformer.RotationPageTransformer
import com.chenxyu.bannerlibrary.transformer.ScalePageTransformer
import com.chenxyu.bannerlibrary.transformer.ZoomOutPageTransformer
import java.lang.ref.WeakReference
import kotlin.math.ceil

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/3/2 0:38
 * @Description:   BannerView基于ViewPager2（支持动画）
 * @Version:       1.0
 */
class BannerView : RelativeLayout {
    companion object {
        const val HORIZONTAL = ViewPager2.ORIENTATION_HORIZONTAL
        const val VERTICAL = ViewPager2.ORIENTATION_VERTICAL
        const val WHAT_NEXT_PAGE = 1
    }

    private var mViewPager2: ViewPager2? = null
    private var mAdapter: Adapter<*, *>? = null

    private var mIndicators: MutableList<ImageView>? = null
    private var mIndicatorUnselected: Int? = null
    private var mIndicatorSelected: Int? = null
    private var mIndicatorWH = 20
    private var mIndicatorMargin = 8
    private var mDataSize: Int = 0
    private var mIndicatorSize: Int = 0

    /**
     * 预加载页面限制（默认2）
     */
    private var mOffscreenPageLimit = 2

    /**
     * 观察Fragment或Activity生命周期控制Banner开始和暂停
     */
    private var mLifecycleOwner: LifecycleOwner? = null

    /**
     * 是否触摸
     */
    private var isTouch = false

    /**
     * 页面切换延迟时间
     */
    private var mDelayMillis: Long = 5000

    /**
     * 滑动持续时间
     */
    private var mDuration: Int? = null

    /**
     * 自动循环轮播（true：自动循环轮播 false：不自动循环轮播）
     */
    private var isAutoPlay: Boolean = true

    /**
     * 指示器
     */
    private var mIndicator: Indicator? = null

    /**
     * Handler控制轮播
     */
    private var mHandler: Handler? = BannerHandler(this)

    /**
     * 观察Activity或Fragment生命周期
     */
    private val mLifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (isAutoPlay) {
                    mHandler?.sendEmptyMessageDelayed(WHAT_NEXT_PAGE, mDelayMillis)
                }
            }
            Lifecycle.Event.ON_PAUSE -> mHandler?.removeMessages(WHAT_NEXT_PAGE)
            Lifecycle.Event.ON_DESTROY -> {
                release()
            }
            else -> {
            }
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.banner_view, this)

        mViewPager2 = findViewById(R.id.view_pager)

        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.BannerView)
        attributes?.let {
            mViewPager2?.orientation = it.getInteger(R.styleable.BannerView_orientation, HORIZONTAL)
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
//            gravity = it.getInteger(R.styleable.BannerView_indicatorGravity, Gravity.CENTER)
//            gravity = it.getInteger(R.styleable.BannerView_indicatorGravity, Gravity.CENTER)
//            visibility = it.getInteger(R.styleable.BannerView_indicatorVisibility, View.VISIBLE)
//            visibility = it.getInteger(R.styleable.BannerView_indicatorVisibility, View.VISIBLE)
            isAutoPlay = it.getBoolean(R.styleable.BannerView_autoPlay, true)
            mOffscreenPageLimit = it.getInteger(R.styleable.BannerView_offscreenPageLimit, mOffscreenPageLimit)
        }
        attributes?.recycle()
    }

    /**
     * 释放资源
     */
    fun release() {
        mHandler?.removeMessages(WHAT_NEXT_PAGE)
        mHandler = null
        mAdapter = null
        mViewPager2 = null
        mIndicators = null
        mIndicatorUnselected = null
        mIndicatorSelected = null
        mIndicatorWH = 0
        mIndicatorMargin = 0
        mDataSize = 0
        mIndicatorSize = 0
        mOffscreenPageLimit = 0
        mLifecycleOwner = null
        mDelayMillis = 0
        mDuration = null
        isTouch = false
        mIndicator = null
        mLifecycleOwner?.lifecycle?.removeObserver(mLifecycleEventObserver)
        mLifecycleOwner = null
    }

    /**
     * 创建Banner
     */
    fun build() {
        if (mAdapter == null) throw NullPointerException("Please set up adapter")
        mAdapter?.getRealData()?.size?.let {
            if (it < 2) throw RuntimeException("No less than 2 pieces of data")
        }

        replaceLayoutManager()
        mViewPager2?.let {
            it.offscreenPageLimit = mOffscreenPageLimit
            it.adapter = mAdapter
            it.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            isTouch = false
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
                            isTouch = false
                        }
                    }
                }
            })

            // 设置指示器
            if (mIndicator != null) {
                mIndicator!!.setIndicator(this, mAdapter!!.getRealItemCount(), mViewPager2!!.orientation)
                mIndicator!!.registerOnPageChangeCallback(mViewPager2)
            } else {
                mIndicator?.unregisterOnPageChangeCallback(mViewPager2)
                mIndicator = null
            }
            // 默认设置第一页
            it.setCurrentItem(1, false)

            if (mLifecycleOwner != null) {
                mLifecycleOwner?.lifecycle?.addObserver(mLifecycleEventObserver)
            } else {
                mHandler?.sendEmptyMessageDelayed(WHAT_NEXT_PAGE, mDelayMillis)
            }
        }
    }

    /**
     * 观察Activity或Fragment生命周期控制Banner开始和暂停
     * @param lifecycleOwner Fragment or Activity
     */
    fun setLifecycle(lifecycleOwner: LifecycleOwner): BannerView {
        mLifecycleOwner = lifecycleOwner
        return this
    }

    /**
     * 设置适配器
     * @param adapter 自定义适配器[Adapter]
     * @param orientation 滑动方向 默认[HORIZONTAL] [VERTICAL]
     */
    fun setAdapter(adapter: Adapter<*, *>, orientation: Int = HORIZONTAL): BannerView {
        mAdapter = adapter.apply {
            mIndicatorSize = getRealItemCount()
            mDataSize = itemCount
        }
        mViewPager2?.orientation = orientation
        return this
    }

    /**
     * 设置指示器
     * @param indicator 自定义指示器需继承此类[Indicator]
     */
    fun setIndicator(indicator: Indicator? = DefaultIndicator()): BannerView {
        mIndicator = indicator
        return this
    }

    /**
     * 是否循环
     */
    fun setAutoPlay(autoPlay: Boolean): BannerView {
        isAutoPlay = autoPlay
        return this
    }

    /**
     * 预加载页面限制（默认2）
     */
    fun setOffscreenPageLimit(@ViewPager2.OffscreenPageLimit limit: Int): BannerView {
        mOffscreenPageLimit = limit
        return this
    }

    /**
     * 页面切换延迟时间
     * @param delayMillis 延迟时间（毫秒）
     */
    fun setDelayMillis(delayMillis: Long): BannerView {
        mDelayMillis = delayMillis
        return this
    }

    /**
     * 滑动持续时间
     * @param duration 持续时间（毫秒）
     */
    fun setDuration(duration: Int): BannerView {
        mDuration = duration
        return this
    }

    /**
     * 设置页面间距
     * @param margin 间距（DP）
     */
    fun setPageMargin(margin: Int): BannerView {
        mViewPager2?.setPageTransformer(MarginPageTransformer(margin.dpToPx(context)))
        return this
    }

    /**
     * Banner圆角
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setRoundRect(radius: Float): BannerView {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(left, top, right, bottom, radius)
            }
        }
        clipToOutline = radius > 0F
        invalidate()
        return this
    }

    /**
     * 一屏多页，在[setAdapter]之后设置
     * @param margin 间距（DP）
     */
    fun setMultiPage(margin: Int): BannerView {
        clipChildren = false
        mViewPager2?.clipChildren = false
        val params = mViewPager2?.layoutParams as MarginLayoutParams
        if (mViewPager2?.orientation == HORIZONTAL) {
            params.leftMargin = margin.dpToPx(context) * 2
            params.rightMargin = params.leftMargin
        } else {
            params.topMargin = margin.dpToPx(context) * 2
            params.bottomMargin = params.topMargin
        }
        return this
    }

    /**
     * 缩放动画，在[setAdapter]之后设置
     */
    fun setScalePageTransformer(): BannerView {
        mViewPager2?.let { setPageTransformer(ScalePageTransformer(it.orientation)) }
        return this
    }

    /**
     * 官方示例缩放动画，在[setAdapter]之后设置
     */
    fun setZoomOutPageTransformer(): BannerView {
        mViewPager2?.let { setPageTransformer(ZoomOutPageTransformer(it.orientation)) }
        return this
    }

    /**
     * 官方示例旋转动画，在[setAdapter]之后设置
     */
    fun setRotationPageTransformer(): BannerView {
        mViewPager2?.let { setPageTransformer(RotationPageTransformer(it.orientation)) }
        return this
    }

    /**
     * 官方示例深度动画，在[setAdapter]之后设置
     */
    fun setDepthPageTransformer(): BannerView {
        mViewPager2?.let { setPageTransformer(DepthPageTransformer(it.orientation)) }
        return this
    }

    /**
     * 设置动画[ZoomOutPageTransformer] [RotationPageTransformer]
     * [DepthPageTransformer] [ScalePageTransformer]
     */
    fun setPageTransformer(transformer: ViewPager2.PageTransformer): BannerView {
        mViewPager2?.setPageTransformer(transformer)
        return this
    }

    /**
     * 开始循环
     */
    fun start() {
        if (mDataSize > 0) {
            mHandler?.sendEmptyMessageDelayed(WHAT_NEXT_PAGE, mDelayMillis)
        }
    }

    /**
     * 暂停循环
     */
    fun pause() {
        if (mDataSize > 0) {
            mHandler?.removeMessages(WHAT_NEXT_PAGE)
        }
    }

    /**
     * 替换LayoutManager
     */
    private fun replaceLayoutManager() {
        mViewPager2?.let {
            val layoutManagerImpl = LayoutManagerImpl(context, it.orientation)
            layoutManagerImpl.mDuration = mDuration
            layoutManagerImpl.mOffscreenPageLimit = mOffscreenPageLimit
            val mRecyclerView = it.getChildAt(0) as RecyclerView
            mRecyclerView.layoutManager = layoutManagerImpl
            val mLayoutManager = it::class.java.getDeclaredField("mLayoutManager")
            mLayoutManager.isAccessible = true
            mLayoutManager.set(it, layoutManagerImpl)
            val field = it::class.java.getDeclaredField("mPageTransformerAdapter")
            field.isAccessible = true
            val mPageTransformerAdapter = field.get(it)
            val mLayoutManager2 = mPageTransformerAdapter::class.java.getDeclaredField("mLayoutManager")
            mLayoutManager2.isAccessible = true
            mLayoutManager2.set(mPageTransformerAdapter, layoutManagerImpl)
            val field2 = it::class.java.getDeclaredField("mScrollEventAdapter")
            field2.isAccessible = true
            val mScrollEventAdapter = field2.get(it)
            val mLayoutManager3 = mScrollEventAdapter::class.java.getDeclaredField("mLayoutManager")
            mLayoutManager3.isAccessible = true
            mLayoutManager3.set(mScrollEventAdapter, layoutManagerImpl)
        }
    }

    class BannerHandler(view: BannerView) : Handler() {
        private val weakReference = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val bannerView = weakReference.get()
            when (msg.what) {
                WHAT_NEXT_PAGE -> {
                    if (bannerView?.isTouch != null && !bannerView.isTouch) {
                        bannerView.mViewPager2?.let {
                            it.currentItem = it.currentItem + 1
                        }
                    }
                    bannerView?.mDelayMillis?.let { sendEmptyMessageDelayed(WHAT_NEXT_PAGE, it) }
                }
            }
        }
    }

    /**
     * 自定义Adapter需要继承此类,使用getReal开头的方法获取真实的数据
     * @param VH ViewHolder
     * @param T 数据类型
     */
    abstract class Adapter<VH : RecyclerView.ViewHolder, T>(
            private val mData: MutableList<T?>
    ) : RecyclerView.Adapter<VH>() {
        private val transformData = mutableListOf<T?>()
        var onItemClickListener: OnItemClickListener? = null
        var onItemLongClickListener: OnItemLongClickListener? = null

        init {
            transformData.addAll(mData)
            if (transformData.size < 1) throw RuntimeException("Minimum size 1")
            transformData.add(0, this.transformData[this.transformData.size - 1])
            transformData.add(this.transformData.size, this.transformData[1])
        }

        /**
         * 处理过的ItemCount
         */
        override fun getItemCount(): Int = transformData.size

        /**
         * 处理过的Data
         */
        fun getData(): MutableList<T?> {
            return transformData
        }

        /**
         * 真实的ItemCount
         */
        fun getRealItemCount(): Int = mData.size

        /**
         * 真实的Data
         */
        fun getRealData(): MutableList<T?> {
            return mData
        }

        /**
         * 真实的Position
         * @param position [onBindViewHolder]里面的position
         */
        fun getRealPosition(position: Int): Int {
            return when (position) {
                0 -> mData.size - 1
                transformData.size - 1 -> 0
                else -> position - 1
            }
        }

        /**
         * 真实的Item
         * @param position [onBindViewHolder] [OnItemClickListener] [OnItemLongClickListener]里面的position
         */
        fun getRealItem(position: Int): T? {
            return when (position) {
                0 -> mData[mData.size - 1]
                transformData.size - 1 -> mData[0]
                else -> mData[position - 1]
            }
        }

        /**
         * 根布局强制MATCH_PARENT
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val bannerViewHolder = onCreateVH(parent, viewType)
            bannerViewHolder.itemView.rootView.apply {
                if (this.layoutParams == null || this.layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT ||
                        this.layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                    val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    this.layoutParams = layoutParams
                }
            }
            return bannerViewHolder
        }

        /**
         * @param parent 绑定到适配器位置后，新视图将被添加到其中的ViewGroup
         * @param viewType 视图类型
         */
        abstract fun onCreateVH(parent: ViewGroup, viewType: Int): VH

        /**
         * ClickListener获取的都是真实position
         */
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(it, getRealPosition(position))
            }
            holder.itemView.setOnLongClickListener {
                onItemLongClickListener?.onItemLongClick(it, getRealPosition(position))
                return@setOnLongClickListener true
            }
            onBindViewHolder(holder, position, transformData[position])
        }

        /**
         * @param holder ViewHolder
         * @param position 当前Item位置
         * @param item 当前Item数据
         */
        abstract fun onBindViewHolder(holder: VH, position: Int, item: T?)
    }

    /**
     * 多布局使用或[RecyclerView.ViewHolder]
     */
    abstract class ViewHolder<in T>(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

        abstract fun initView(item: T?, position: Int? = null, context: Context? = null)
    }

    private class LayoutManagerImpl(context: Context, orientation: Int, reverseLayout: Boolean = false) : LinearLayoutManager(
            context,
            orientation,
            reverseLayout
    ) {
        /**
         * 滑动持续时间
         */
        var mDuration: Int? = null

        /**
         * 预加载页面限制（默认2）
         */
        var mOffscreenPageLimit = 2

        override fun smoothScrollToPosition(
                recyclerView: RecyclerView?,
                state: RecyclerView.State?,
                position: Int
        ) {
            val linearSmoothScroller = object : LinearSmoothScroller(recyclerView!!.context) {
                override fun calculateTimeForDeceleration(dx: Int): Int {
                    return mDuration ?: ceil(calculateTimeForScrolling(dx) / .3356).toInt()
                }
            }
            linearSmoothScroller.targetPosition = position
            startSmoothScroll(linearSmoothScroller)
        }

        override fun calculateExtraLayoutSpace(state: RecyclerView.State,
                                               extraLayoutSpace: IntArray) {
            val pageLimit: Int = mOffscreenPageLimit
            if (pageLimit == ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT) {
                // Only do custom prefetching of offscreen pages if requested
                super.calculateExtraLayoutSpace(state, extraLayoutSpace)
                return
            }
//            val offscreenSpace: Int = getPageSize() * pageLimit
//            extraLayoutSpace[0] = offscreenSpace
//            extraLayoutSpace[1] = offscreenSpace
        }

        override fun requestChildRectangleOnScreen(parent: RecyclerView,
                                                   child: View, rect: Rect, immediate: Boolean,
                                                   focusedChildVisible: Boolean): Boolean {
            return false // users should use setCurrentItem instead
        }
    }

}
