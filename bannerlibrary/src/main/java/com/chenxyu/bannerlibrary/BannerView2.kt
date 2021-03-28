package com.chenxyu.bannerlibrary

import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import com.chenxyu.bannerlibrary.extend.az
import com.chenxyu.bannerlibrary.extend.dpToPx
import com.chenxyu.bannerlibrary.indicator.DefaultIndicator
import com.chenxyu.bannerlibrary.indicator.Indicator
import com.chenxyu.bannerlibrary.listener.OnItemClickListener
import com.chenxyu.bannerlibrary.listener.OnItemLongClickListener
import java.lang.ref.WeakReference

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/12 15:08
 * @Description:   BannerView2基于RecyclerView（不支持动画），isAutoPlay值null时可以设置ItemView的Margin（不循环）。
 * @Version:       1.0
 */
class BannerView2 : RelativeLayout {
    companion object {
        private const val WHAT_NEXT_PAGE = 1
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        const val VERTICAL = RecyclerView.VERTICAL
    }

    private var mRecyclerView: RecyclerView? = null
    private var mPagerSnapHelper: PagerSnapHelper? = null
    private var mLayoutManager: LayoutManagerImpl? = null
    private var mAdapter: Adapter<*, *>? = null

    /**
     * 观察Fragment或Activity生命周期控制Banner开始和暂停
     */
    private var mLifecycleOwner: LifecycleOwner? = null

    /**
     * 自动循环轮播（true：自动-循环轮播 false：不自动-循环轮播 null：默认不循环）
     */
    private var isAutoPlay: Boolean? = null

    /**
     *当前页面数据长度
     */
    private var mDataSize: Int = -1

    /**
     * 一屏显示个数
     */
    private var mShowCount: Int = 1

    /**
     * 页面切换延迟时间
     */
    private var mDelayMillis: Long = 5000L

    /**
     * 滑动持续时间
     */
    private var mDuration: Int? = null

    /**
     * 是否触摸
     */
    private var isTouch = false

    /**
     * 指示器
     */
    private var mIndicator: Indicator? = null

    /**
     * 指示器是否循环
     */
    private var isLoopForIndicator: Boolean = false

    /**
     * 指示器外边距
     */
    private var mIndicatorMargin: Int? = null

    /**
     * 指示器位置
     */
    private var mIndicatorGravity: Int? = null

    /**
     * 默认Indicator
     */
    @DrawableRes
    private var mIndicatorNormal: Int? = null

    /**
     * 选中Indicator
     */
    @DrawableRes
    private var mIndicatorSelected: Int? = null

    /**
     * Handler控制轮播
     */
    private var mHandler: BannerHandler? = BannerHandler(this)

    /**
     * 观察Activity或Fragment生命周期
     */
    private val mLifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (isAutoPlay != null && isAutoPlay == true) {
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

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.BannerView2)
        var orientation: Int = HORIZONTAL
        attributes.let {
            if (it.hasValue(R.styleable.BannerView2_orientation)) {
                orientation = it.getInteger(R.styleable.BannerView2_orientation, HORIZONTAL)
            }
            if (it.hasValue(R.styleable.BannerView2_indicatorNormal)) {
                mIndicatorNormal = it.getResourceId(R.styleable.BannerView2_indicatorNormal, -1)
            }
            if (it.hasValue(R.styleable.BannerView2_indicatorSelected)) {
                mIndicatorSelected = it.getResourceId(R.styleable.BannerView2_indicatorSelected, -1)
            }
            if (it.hasValue(R.styleable.BannerView2_indicatorMargin)) {
                mIndicatorMargin = it.getDimension(R.styleable.BannerView2_indicatorMargin, -1F).toInt()
            }
            if (it.hasValue(R.styleable.BannerView2_indicatorGravity)) {
                mIndicatorGravity = it.getInteger(R.styleable.BannerView2_indicatorGravity, -1)
            }
            if (it.hasValue(R.styleable.BannerView2_delayMillis)) {
                mDelayMillis = it.getInteger(R.styleable.BannerView2_delayMillis, 5000).toLong()
            }
            if (it.hasValue(R.styleable.BannerView2_duration)) {
                mDuration = it.getInteger(R.styleable.BannerView2_duration, 0)
            }
        }
        attributes.recycle()

        mLayoutManager = LayoutManagerImpl(context, orientation, false)
        mRecyclerView = RecyclerView(context)
        mRecyclerView?.id = R.id.recycler_view_id
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        addView(mRecyclerView, 0, layoutParams)
    }

    /**
     * 释放资源
     */
    fun release() {
        mHandler?.removeMessages(WHAT_NEXT_PAGE)
        mHandler = null
        mPagerSnapHelper?.attachToRecyclerView(null)
        mPagerSnapHelper = null
        mRecyclerView?.clearOnScrollListeners()
        mRecyclerView = null
        mLayoutManager = null
        mAdapter?.getData()?.clear()
        mAdapter?.getRealData()?.clear()
        mAdapter = null
        isAutoPlay = null
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
            if (isAutoPlay != null && it < 1) throw RuntimeException("No less than 1 pieces of data")
        }
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val displayMetrics = resources.displayMetrics

        mHandler?.removeMessages(WHAT_NEXT_PAGE)
        mLayoutManager?.mDuration = mDuration
        mRecyclerView?.apply {
            layoutManager = mLayoutManager
            mAdapter?.let {
                it.orientation = mLayoutManager?.orientation
                if (this@BannerView2.measuredWidth == 0) {
                    it.bannerWidth = displayMetrics.widthPixels
                } else {
                    it.bannerWidth = this@BannerView2.measuredWidth
                }
                if (this@BannerView2.measuredHeight == 0) {
                    it.bannerHeight = displayMetrics.heightPixels
                } else {
                    it.bannerHeight = this@BannerView2.measuredHeight
                }
                adapter = it
            }
        }

        mRecyclerView?.clearOnScrollListeners()
        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    // 闲置
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        isTouch = false
                        toggleStartEndPage()
                    }
                    // 拖拽中
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        isTouch = true
                    }
                    // 惯性滑动中
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        isTouch = false
                    }
                }
            }
        })

        // 设置指示器
        if (mIndicator != null || mIndicatorNormal != null || mIndicatorSelected != null ||
                mIndicatorMargin != null || mIndicatorGravity != null) {
            if (mIndicator == null) {
                mIndicator = DefaultIndicator(true, mIndicatorNormal, mIndicatorSelected,
                        mIndicatorMargin, mIndicatorGravity)
            }
            mIndicator!!.initialize(this, mAdapter!!.getRealItemCount(),
                    mLayoutManager!!.orientation, isLoopForIndicator)
            mIndicator!!.addOnScrollListener(mRecyclerView)
        } else {
            mIndicator?.removeScrollListener(mRecyclerView)
            mIndicator = null
        }

        if (isAutoPlay != null) {
            // 默认设置第一页
            mRecyclerView?.scrollToPosition(1)
            if (mPagerSnapHelper == null) {
                mPagerSnapHelper = PagerSnapHelper()
                mPagerSnapHelper?.attachToRecyclerView(mRecyclerView)
            }
            if (isAutoPlay == true) mHandler?.sendEmptyMessageDelayed(WHAT_NEXT_PAGE, mDelayMillis)
        } else {
            mHandler?.removeMessages(WHAT_NEXT_PAGE)
            mPagerSnapHelper?.attachToRecyclerView(null)
            mPagerSnapHelper = null
        }
    }

    /**
     * 观察Activity或Fragment生命周期控制Banner开始和暂停
     * @param lifecycleOwner Fragment or Activity
     */
    fun setLifecycle(lifecycleOwner: LifecycleOwner): BannerView2 {
        if (mLifecycleOwner == null) {
            mLifecycleOwner = lifecycleOwner
            mLifecycleOwner?.lifecycle?.addObserver(mLifecycleEventObserver)
        }
        return this
    }

    /**
     * 设置方向
     * @param orientation 布局方向默认[BannerView2.HORIZONTAL] [BannerView2.VERTICAL]
     * @param reverseLayout 颠倒布局
     */
    fun setOrientation(orientation: Int, reverseLayout: Boolean = false): BannerView2 {
        mLayoutManager?.apply {
            this.orientation = orientation
            this.reverseLayout = reverseLayout
        }
        return this
    }

    /**
     * 设置适配器
     * @param adapter 自定义适配器[Adapter]
     * @param margins 边距（DP）[Margins]
     */
    fun setAdapter(
            adapter: Adapter<*, *>,
            margins: Margins? = null
    ): BannerView2 {
        mAdapter = adapter.apply {
            autoPlay(isAutoPlay, mShowCount)
            mDataSize = itemCount
        }
        margins?.let { mAdapter?.margins = it }
        return this
    }

    /**
     * 设置指示器
     * @param indicator 自定义指示器需继承此类[Indicator]
     */
    fun setIndicator(indicator: Indicator): BannerView2 {
        mIndicator = indicator
        return this
    }

    /**
     * 自动循环轮播
     * @param autoPlay true：自动-循环轮播 false：不自动-循环轮播 null：默认不循环
     */
    fun setAutoPlay(autoPlay: Boolean? = null): BannerView2 {
        isAutoPlay = autoPlay
        mAdapter?.autoPlay(isAutoPlay, mShowCount)
        isLoopForIndicator = autoPlay != null
        return this
    }

    /**
     * 页面切换延迟时间
     * @param delayMillis 延迟时间（毫秒）
     */
    fun setDelayMillis(delayMillis: Long): BannerView2 {
        mDelayMillis = delayMillis
        return this
    }

    /**
     * 滑动持续时间
     * @param duration 持续时间（毫秒）
     */
    fun setDuration(duration: Int): BannerView2 {
        mDuration = duration
        return this
    }

    /**
     * Banner圆角
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setRoundRect(radius: Float): BannerView2 {
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
     * 显示一屏显示个数（默认1个）
     * @param count 显示个数
     */
    fun setShowCount(count: Int): BannerView2 {
        mShowCount = count
        mAdapter?.autoPlay(isAutoPlay, mShowCount)
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
     * 循环轮播切换前后假页面
     */
    private fun toggleStartEndPage() {
        if (isAutoPlay != null) {
            val currentPosition = mRecyclerView?.getChildAt(0)
                    ?.layoutParams?.az<RecyclerView.LayoutParams>()?.viewAdapterPosition
            mAdapter?.getData()?.size?.let {
                when (currentPosition) {
                    // 第一页（假）
                    0 -> {
                        mRecyclerView?.scrollToPosition(it - 2)
                    }
                    // 最后一页（假）
                    it - 1 -> {
                        mRecyclerView?.scrollToPosition(1)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    class BannerHandler(view: BannerView2) : Handler(Looper.getMainLooper()) {
        private val weakReference = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val bannerView = weakReference.get()
            when (msg.what) {
                WHAT_NEXT_PAGE -> {
                    bannerView?.mRecyclerView?.let {
                        if (!bannerView.isTouch) {
                            it.getChildAt(0).layoutParams
                                    .az<RecyclerView.LayoutParams>()
                                    ?.viewAdapterPosition?.plus(1)?.let { position ->
                                        it.smoothScrollToPosition(position)
                                    }

                        }
                        sendEmptyMessageDelayed(WHAT_NEXT_PAGE, bannerView.mDelayMillis)
                    }
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

        /**
         * 自动循环轮播（true：自动-循环轮播 false：不自动-循环轮播 null：默认不循环）
         */
        private var autoPlay: Boolean? = null

        /**
         * 间距
         */
        var margins: Margins? = null

        /**
         * 一屏显示个数
         */
        var showCount: Int = 1

        /**
         * 方向
         */
        var orientation: Int? = HORIZONTAL

        /**
         * BannerView2的宽
         */
        var bannerWidth: Int = 0

        /**
         * BannerView2的高
         */
        var bannerHeight: Int = 0

        init {
            transformData.addAll(mData)
        }

        /**
         * 是否循环轮播
         */
        fun autoPlay(autoPlay: Boolean?, showCount: Int) {
            this.autoPlay = autoPlay
            this.showCount = showCount
            when {
                autoPlay != null && transformData.size == mData.size -> {
                    // 循环轮播前后增加一页
                    transformData.add(0, transformData[transformData.size - 1])
                    transformData.add(transformData.size, transformData[1])
                }
                autoPlay == null -> {
                    transformData.clear()
                    transformData.addAll(mData)
                }
            }
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
            return if (autoPlay != null) {
                when (position) {
                    0 -> mData.size - 1
                    transformData.size - 1 -> 0
                    else -> position - 1
                }
            } else {
                position
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
         * 返回显示的ItemView position
         */
        override fun getItemViewType(position: Int): Int {
            return position
        }

        /**
         * 根布局强制MATCH_PARENT
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val bannerViewHolder = onCreateVH(parent, viewType)
            bannerViewHolder.itemView.rootView?.apply {
                if (autoPlay != null) {
                    // 循环轮播强制MATCH_PARENT
                    bannerViewHolder.itemView.rootView.apply {
                        val layoutParams = RecyclerView.LayoutParams(
                                RecyclerView.LayoutParams.MATCH_PARENT,
                                RecyclerView.LayoutParams.MATCH_PARENT
                        )
                        this.layoutParams = layoutParams
                    }
                } else {
                    // 非循环轮播
                    var width: Int? = null
                    var height: Int? = null
                    // 显示个数
                    if (showCount > 1) {
                        when (orientation) {
                            HORIZONTAL -> {
                                width = bannerWidth.div(showCount)
                            }
                            VERTICAL -> {
                                height = bannerHeight.div(showCount)
                            }
                        }
                    }
                    val layoutParams = RecyclerView.LayoutParams(
                            width ?: RecyclerView.LayoutParams.MATCH_PARENT,
                            height ?: RecyclerView.LayoutParams.MATCH_PARENT
                    )
                    // 设置Margin
                    margins?.let {
                        layoutParams.setMargins(
                                it.leftMargin.dpToPx(context),
                                it.topMargin.dpToPx(context),
                                it.rightMargin.dpToPx(context),
                                it.bottomMargin.dpToPx(context)
                        )
                    }
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

    /**
     * 外边距（DP）
     */
    data class Margins(
            var leftMargin: Int = 0, var topMargin: Int = 0,
            var rightMargin: Int = 0, var bottomMargin: Int = 0
    )

    private class LayoutManagerImpl(context: Context, orientation: Int, reverseLayout: Boolean = false) : LinearLayoutManager(
            context,
            orientation,
            reverseLayout
    ) {
        /**
         * 滑动持续时间
         */
        var mDuration: Int? = null

        override fun smoothScrollToPosition(
                recyclerView: RecyclerView?,
                state: RecyclerView.State?,
                position: Int
        ) {
            val linearSmoothScroller = object : LinearSmoothScroller(recyclerView!!.context) {
                override fun calculateTimeForDeceleration(dx: Int): Int {
                    return mDuration ?: super.calculateTimeForDeceleration(dx)
                }
            }
            linearSmoothScroller.targetPosition = position
            startSmoothScroll(linearSmoothScroller)
        }
    }
}