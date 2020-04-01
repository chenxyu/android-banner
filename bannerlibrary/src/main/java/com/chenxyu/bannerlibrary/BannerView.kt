package com.chenxyu.bannerlibrary

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.request.RequestOptions
import java.lang.ref.WeakReference


/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/3/2 0:38
 * @Description:
 * @Version:       1.0
 */
class BannerView : LinearLayout {
    private var mContext: Context? = null
    private var mRootLayout: RelativeLayout? = null
    private var mViewPager: ViewPager2? = null
    private var mIndicatorLayout: LinearLayout? = null
    private val mImages = mutableListOf<Any>()
    private val mIndicators = mutableListOf<ImageView>()
    private var mOnItemClickListener: OnItemClickListener? = null
    private var isTouch = false
    private var placeholder: Int? = null
    private var error: Int? = null
    private var scaleType: ScaleType? = null
    private var requestOptions: RequestOptions? = null

    /**
     * 页面切换时间(请在addUrl前设置)
     */
    var mDelayMillis: Long = 5000
    private var mAdapter: BannerAdapter? = null
    private val mHandler: Handler = BannerHandler(this)

    class BannerHandler(view: BannerView) : Handler() {
        private val weakReference = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val bannerView = weakReference.get()
            if (bannerView?.isTouch != null && !bannerView.isTouch) {
                bannerView.mViewPager?.let {
                    if (it.currentItem == it.childCount.minus(1)) {
                        it.currentItem = 1
                    } else {
                        it.currentItem = it.currentItem.plus(1)
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
    }

    private fun init() {
        val view = View.inflate(mContext, R.layout.banner_view, null)
        addView(view)
        mRootLayout = view.findViewById(R.id.root_layout)
        mViewPager = view.findViewById(R.id.view_pager)
        mIndicatorLayout = view.findViewById(R.id.indicator_layout)
    }

    private fun initIndicator(size: Int) {
        repeat(size) {
            val indicators = ImageView(mContext)
            indicators.setImageResource(R.drawable.banner_indicator_gray)
            val layoutParams = LayoutParams(16, 16)
            layoutParams.setMargins(6, 0, 6, 0)
            indicators.layoutParams = layoutParams
            mIndicatorLayout?.addView(indicators)
            mIndicators.add(indicators)
        }
        mIndicators[0].setImageResource(R.drawable.banner_indicator_white)
    }

    private fun initViewPager() {
        mViewPager?.let {
            it.offscreenPageLimit = 2
            it.adapter = mAdapter
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
                                it.setCurrentItem(mImages.size - 2, false)
                            }
                            if (it.currentItem == mImages.size - 1) {
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
                    for (indicator in mIndicators) {
                        indicator.setImageResource(R.drawable.banner_indicator_gray)
                    }
                    for (i in mIndicators.indices) {
                        when {
                            position == 0 -> {
                                mIndicators[mIndicators.size - 1].setImageResource(R.drawable.banner_indicator_white)
                                return
                            }
                            position == mImages.size - 1 -> {
                                mIndicators[0].setImageResource(R.drawable.banner_indicator_white)
                                return
                            }
                            position == mImages.size - 2 -> {
                                mIndicators[mIndicators.size - 1].setImageResource(R.drawable.banner_indicator_white)
                                return
                            }
                            i == position -> {
                                mIndicators[i - 1].setImageResource(R.drawable.banner_indicator_white)
                                return
                            }
                        }
                    }
                }
            })
            mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
        }
    }

    /**
     * 根据Fragment或Activity生命周期控制Banner开始和暂停
     * @param lifecycleOwner Fragment or Activity
     */
    fun setLifecycle(lifecycleOwner: LifecycleOwner): BannerView {
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
                Lifecycle.Event.ON_PAUSE -> mHandler.removeMessages(0)
                else -> {
                }
            }
        })
        return this
    }

    /**
     * 如果你需要自定义Glide RequestOptions
     * setRequestOptions优先setPlaceholder()setError()
     */
    fun setRequestOptions(requestOptions: RequestOptions): BannerView {
        this.requestOptions = requestOptions
        return this
    }

    /**
     * Indicator显示或隐藏
     * @param visibility View.GONE or View.VISIBLE or View.INVISIBLE
     */
    fun setIndicatorVisibility(visibility: Int): BannerView {
        mIndicatorLayout!!.visibility = visibility
        return this
    }

    /**
     * 设置Indicator位置(左中右)
     * 默认中
     * @param gravity 位置
     */
    fun setIndicatorGravity(gravity: Int): BannerView {
        mIndicatorLayout!!.gravity = gravity
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
     * 一屏多页
     * @param marginPx 间距Px
     */
    fun setMultiPage(marginPx: Int): BannerView {
        mRootLayout?.clipChildren = false
        mViewPager?.clipChildren = false
        val params = mViewPager?.layoutParams as MarginLayoutParams
        params.leftMargin = marginPx * 2
        params.rightMargin = params.leftMargin
        return this
    }

    /**
     * 页面缩放动画
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setCompositePageTransformer(): BannerView {
        mViewPager?.setPageTransformer(ScaleInTransformer())
        return this
    }

    /**
     * 占位符
     */
    fun setPlaceholder(placeholder: Int?): BannerView {
        this.placeholder = placeholder
        return this
    }

    /**
     * 错误时显示图片
     */
    fun setError(error: Int?): BannerView {
        this.error = error
        return this
    }

    /**
     * 图片显示模式
     */
    fun setScaleType(scaleType: ScaleType?): BannerView {
        this.scaleType = scaleType
        return this
    }

    /**
     * 添加RES资源图片(本地图片)
     * @param resIds 图片资源ID
     */
    fun setImageRes(resIds: MutableList<Int?>): BannerView {
        if (resIds.size < 1) throw RuntimeException("Minimum 1 pictures")
        initIndicator(resIds.size)
        resIds.add(0, resIds[resIds.size - 1])
        resIds.add(resIds.size, resIds[1])
        resIds.forEach { resId ->
            resId?.let { mImages.add(it) }
        }
        return this
    }

    /**
     * 添加网络图片或本地图片
     * @param urls 图片URL
     */
    fun setUrls(urls: MutableList<String?>): BannerView {
        if (urls.size < 1) throw RuntimeException("Minimum 1 pictures")
        initIndicator(urls.size)
        urls.add(0, urls[urls.size - 1])
        urls.add(urls.size, urls[1])
        urls.forEach { url ->
            url?.let { mImages.add(it) }
        }
        return this
    }

    /**
     * 添加一个Item点击事件
     */
    fun setOnItemClickListener(listener: OnItemClickListener?): BannerView {
        this.mOnItemClickListener = listener
        return this
    }

    fun build() {
        mAdapter = BannerAdapter(mContext, mImages, placeholder,
                error, scaleType, requestOptions, mOnItemClickListener)
        initViewPager()
    }

    interface OnItemClickListener {
        /**
         * @param view itemView
         * @param position 1 2 3 ...
         */
        fun onItemClick(view: View?, position: Int)
    }

}