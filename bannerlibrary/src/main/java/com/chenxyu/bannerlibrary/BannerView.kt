package com.chenxyu.bannerlibrary

import android.annotation.SuppressLint
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
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2


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
    /**
     * 页面切换时间(请在addUrl前设置)
     */
    var mDelayMillis: Long = 5000
    private var mAdapter: BannerAdapter? = null
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            if (!isTouch) {
                mViewPager?.let {
                    if (it.currentItem == it.childCount.minus(1)) {
                        it.currentItem = 1
                    } else {
                        it.currentItem = it.currentItem.plus(1)
                    }
                }
            }
            sendEmptyMessageDelayed(0, mDelayMillis)
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
     * 添加一个Item点击事件
     *
     * @param listener 点击事件
     */
    fun addOnItemClickListener(listener: OnItemClickListener?) {
        this.mOnItemClickListener = listener
    }

    /**
     * 设置Indicator位置(左中右)
     * 默认中
     *
     * @param gravity 位置
     */
    fun setIndicatorGravity(gravity: Int) {
        mIndicatorLayout!!.gravity = gravity
    }

    /**
     * 设置页面间距(请在addUrl前设置)
     *
     * @param marginPx 间距Px
     */
    fun setPageMargin(marginPx: Int) {
        mViewPager?.setPageTransformer(MarginPageTransformer(marginPx))
    }

    /**
     * 一屏多页(请在addUrl前设置)
     *
     * @param marginPx 间距Px
     */
    fun setMultiPage(marginPx: Int) {
        mRootLayout?.clipChildren = false
        mViewPager?.clipChildren = false
        val params = mViewPager?.layoutParams as MarginLayoutParams
        params.leftMargin = marginPx * 2
        params.rightMargin = params.leftMargin
    }

    /**
     * 页面缩放动画(请在addUrl前设置)
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setCompositePageTransformer() {
        mViewPager?.setPageTransformer(ScaleInTransformer())
    }

    /**
     * 添加RES资源图片(本地图片)
     *
     * @param resIds 图片资源ID
     */
    fun addImageRes(resIds: MutableList<Int?>, placeholder: Int?, error: Int?) {
        if (resIds.size < 2) throw RuntimeException("Minimum 2 pictures")
        addImageRes(resIds, placeholder, error, null)
    }

    /**
     * 添加RES资源图片(本地图片)
     *
     * @param resIds    图片资源ID
     * @param scaleType ImageView的ScaleType
     */
    fun addImageRes(resIds: MutableList<Int?>, placeholder: Int?, error: Int?, scaleType: ScaleType?) {
        if (resIds.size < 2) throw RuntimeException("Minimum 2 pictures")
        initIndicator(resIds.size)
        resIds.add(0, resIds[resIds.size - 1])
        resIds.add(resIds.size, resIds[1])
        resIds.forEach { resId ->
            resId?.let { mImages.add(it) }
        }
        mAdapter = BannerAdapter(mContext, mImages, placeholder,
                error, scaleType, mOnItemClickListener)
        initViewPager()
    }

    /**
     * 添加网络图片或本地图片
     *
     * @param urls        图片URL
     * @param placeholder 加载前图片
     * @param error       加载错误图片
     */
    fun addUrl(urls: MutableList<String?>, placeholder: Int, error: Int) {
        if (urls.size < 2) throw RuntimeException("Minimum 2 pictures")
        addUrl(urls, placeholder, error, null)
    }

    /**
     * 添加网络图片或本地图片
     *
     * @param urls        图片URL
     * @param placeholder 加载前图片
     * @param error       加载错误图片
     * @param scaleType   ImageView的ScaleType
     */
    fun addUrl(urls: MutableList<String?>, placeholder: Int?, error: Int?, scaleType: ScaleType?) {
        if (urls.size < 2) throw RuntimeException("Minimum 2 pictures")
        initIndicator(urls.size)
        urls.add(0, urls[urls.size - 1])
        urls.add(urls.size, urls[1])
        urls.forEach { url ->
            url?.let { mImages.add(it) }
        }
        mAdapter = BannerAdapter(mContext, mImages, placeholder,
                error, scaleType, mOnItemClickListener)
        initViewPager()
    }

    fun onResume() {
        mHandler.sendEmptyMessageDelayed(0, mDelayMillis)
    }

    fun onPause() {
        mHandler.removeMessages(0)
    }

    interface OnItemClickListener {
        /**
         * 0,1,2,3...
         */
        fun onItemClick(position: Int)
    }
}