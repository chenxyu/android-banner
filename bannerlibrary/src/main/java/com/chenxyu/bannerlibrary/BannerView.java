package com.chenxyu.bannerlibrary;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by ChenXingYu on 16/5/16.
 */
public class BannerView extends LinearLayout implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private BannerViewPager mBannerViewPager;
    private LinearLayout mRootIndicator;
    private ImageView mIndicator1;
    private ImageView mIndicator2;
    private ImageView mIndicator3;
    private ImageView mIndicator4;
    private ImageView mIndicator5;
    private ArrayList<ImageView> mImageViews = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;
    private FixedSpeedScroller mScroller;
    private int mTouchDuration = 250;
    private int mUpDuration = 400;

    private PagerAdapter mAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mImageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(mImageViews.get(position));
            mImageViews.get(position).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(position);
                }
            });
            return mImageViews.get(position);
        }
    };

    public BannerView(Context context) {
        super(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        init();
    }

    private void init() {
        View view = inflate(mContext, R.layout.banner_view, null);
        addView(view);
        mBannerViewPager = (BannerViewPager) view.findViewById(R.id.banner_view_pager);
        mRootIndicator = (LinearLayout) view.findViewById(R.id.root_indicator);
        mIndicator1 = (ImageView) view.findViewById(R.id.indicator1);
        mIndicator2 = (ImageView) view.findViewById(R.id.indicator2);
        mIndicator3 = (ImageView) view.findViewById(R.id.indicator3);
        mIndicator4 = (ImageView) view.findViewById(R.id.indicator4);
        mIndicator5 = (ImageView) view.findViewById(R.id.indicator5);
    }

    private void initBanner() {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            mScroller = new FixedSpeedScroller(mBannerViewPager.getContext(), new AccelerateInterpolator());
            field.set(mBannerViewPager, mScroller);
            mScroller.setDuration(mUpDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBannerViewPager.setAdapter(mAdapter);
        mBannerViewPager.setCurrentItem(1);
        mBannerViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mIndicator1.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator2.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator3.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator4.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator5.setImageResource(R.drawable.banner_indicator_white);
                break;

            case 1:
                mIndicator1.setImageResource(R.drawable.banner_indicator_white);
                mIndicator2.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator3.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator4.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator5.setImageResource(R.drawable.banner_indicator_gray);
                break;

            case 2:
                mIndicator1.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator2.setImageResource(R.drawable.banner_indicator_white);
                mIndicator3.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator4.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator5.setImageResource(R.drawable.banner_indicator_gray);
                break;

            case 3:
                mIndicator1.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator2.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator3.setImageResource(R.drawable.banner_indicator_white);
                mIndicator4.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator5.setImageResource(R.drawable.banner_indicator_gray);
                break;

            case 4:
                mIndicator1.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator2.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator3.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator4.setImageResource(R.drawable.banner_indicator_white);
                mIndicator5.setImageResource(R.drawable.banner_indicator_gray);
                break;

            case 5:
                mIndicator1.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator2.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator3.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator4.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator5.setImageResource(R.drawable.banner_indicator_white);
                break;

            case 6:
                mIndicator1.setImageResource(R.drawable.banner_indicator_white);
                mIndicator2.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator3.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator4.setImageResource(R.drawable.banner_indicator_gray);
                mIndicator5.setImageResource(R.drawable.banner_indicator_gray);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                mScroller.setDuration(mUpDuration);
                switch (mBannerViewPager.getCurrentItem()) {
                    case 0:
                        mBannerViewPager.setCurrentItem(5, false);
                        break;

                    case 6:
                        mBannerViewPager.setCurrentItem(1, false);
                        break;
                }
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                mScroller.setDuration(mTouchDuration);
                break;
        }
    }


    /**
     * 添加一个Item点击监听(对应1,2,3,4,5 不是0,1,2,3,4)
     *
     * @param listener
     */
    public void addOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 设置Indicator位置(左中右)
     * 默认中
     *
     * @param gravity
     */
    public void setIndicatorGravity(int gravity) {
        mRootIndicator.setGravity(gravity);
    }

    /**
     * 设置页面切换时间(请在addUrl前设置)
     *
     * @param time
     */
    public void setTimeDelayed(int time) {
        mBannerViewPager.setTimeDelayed(time);
    }

    /**
     * 设置ViewPager切换速度(请在addUrl前设置)
     *
     * @param touchTime 手动切换速度
     * @param upTime    自动切换速度
     */
    public void setDuration(int touchTime, int upTime) {
        this.mTouchDuration = touchTime;
        this.mUpDuration = upTime;
    }

    /**
     * 添加RES资源图片
     *
     * @param resId
     * @param placeholder
     * @param error
     */
    public void addImageRes(ArrayList<Integer> resId, int placeholder, int error) {
        addImageRes(resId, placeholder, error, null);
    }

    /**
     * 添加RES资源图片
     *
     * @param resId
     * @param placeholder
     * @param error
     * @param scaleType
     */
    public void addImageRes(ArrayList<Integer> resId, int placeholder, int error, ImageView.ScaleType scaleType) {
        resId.add(0, resId.get(resId.size() - 1));
        resId.add(resId.size(), resId.get(1));

        for (Integer res : resId) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(res);
            if (scaleType != null)
                imageView.setScaleType(scaleType);
            mImageViews.add(imageView);
        }

        initBanner();
    }

    /**
     * 添加网络图片或本地图片
     *
     * @param url
     * @param placeholder 加载前图片
     * @param error       加载错误图片
     */
    public void addUrl(ArrayList<String> url, int placeholder, int error) {
        addUrl(url, placeholder, error, null);
    }

    /**
     * 添加网络图片或本地图片
     *
     * @param url
     * @param placeholder 加载前图片
     * @param error       加载错误图片
     * @param scaleType   ImageView的ScaleType
     */
    public void addUrl(ArrayList<String> url, int placeholder, int error, ImageView.ScaleType scaleType) {
        url.add(0, url.get(url.size() - 1));
        url.add(url.size(), url.get(1));

        for (int i = 0; i < url.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            if (scaleType != null)
                imageView.setScaleType(scaleType);

            Glide.with(mContext)
                    .load(url.get(i))
                    .placeholder(placeholder)
                    .error(error)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            mImageViews.add(imageView);
        }

        initBanner();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
