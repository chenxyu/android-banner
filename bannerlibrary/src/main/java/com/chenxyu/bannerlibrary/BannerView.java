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
    private ArrayList<ImageView> mImageViews = new ArrayList<>();
    private ArrayList<ImageView> mIndicators = new ArrayList<>();
    private ArrayList<ImageView> mIndicators2 = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;
//    private FixedSpeedScroller mScroller;
//    private int mTouchDuration = 250;
//    private int mUpDuration = 400;

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
        mIndicators2.add((ImageView) view.findViewById(R.id.indicator1));
        mIndicators2.add((ImageView) view.findViewById(R.id.indicator2));
        mIndicators2.add((ImageView) view.findViewById(R.id.indicator3));
        mIndicators2.add((ImageView) view.findViewById(R.id.indicator4));
        mIndicators2.add((ImageView) view.findViewById(R.id.indicator5));
        mIndicators2.add((ImageView) view.findViewById(R.id.indicator6));
    }

    private void initBanner() {
//        try {
//            Field field = ViewPager.class.getDeclaredField("mScroller");
//            field.setAccessible(true);
//            mScroller = new FixedSpeedScroller(mBannerViewPager.getContext(), new AccelerateInterpolator());
//            field.set(mBannerViewPager, mScroller);
//            mScroller.setDuration(mUpDuration);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mBannerViewPager.setAdapter(mAdapter);
        mBannerViewPager.setCurrentItem(1);
        mBannerViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (ImageView indicator : mIndicators) {
            indicator.setImageResource(R.drawable.banner_indicator_gray);
        }
        for (int i = 0; i < mIndicators.size(); i++) {
            if (position == 0) {
                mIndicators.get(mIndicators.size() - 1).setImageResource(R.drawable.banner_indicator_white);
                return;
            } else if (position == mImageViews.size() - 1) {
                mIndicators.get(0).setImageResource(R.drawable.banner_indicator_white);
                return;
            } else if (position == mImageViews.size() - 2) {
                mIndicators.get(mIndicators.size() - 1).setImageResource(R.drawable.banner_indicator_white);
                return;
            } else if (i == position) {
                mIndicators.get(i - 1).setImageResource(R.drawable.banner_indicator_white);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
//                mScroller.setDuration(mUpDuration);
                if (mBannerViewPager.getCurrentItem() == 0) {
                    mBannerViewPager.setCurrentItem(mImageViews.size() - 2, false);
                }
                if (mBannerViewPager.getCurrentItem() == mImageViews.size() - 1) {
                    mBannerViewPager.setCurrentItem(1, false);
                }
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
//                mScroller.setDuration(mTouchDuration);
                break;
        }
    }


    /**
     * 添加一个Item点击事件(对应1,2,3,4,5 不是0,1,2,3,4)
     *
     * @param listener 点击事件
     */
    public void addOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 设置Indicator位置(左中右)
     * 默认中
     *
     * @param gravity 位置
     */
    public void setIndicatorGravity(int gravity) {
        mRootIndicator.setGravity(gravity);
    }

    /**
     * 设置页面切换时间(请在addUrl前设置)
     *
     * @param time 页面切换时间
     */
    public void setTimeDelayed(int time) {
        mBannerViewPager.setTimeDelayed(time);
    }

//    /**
//     * 设置ViewPager切换速度(请在addUrl前设置)
//     *
//     * @param touchTime 手动切换速度
//     * @param upTime    自动切换速度
//     */
//    public void setDuration(int touchTime, int upTime) {
//        this.mTouchDuration = touchTime;
//        this.mUpDuration = upTime;
//    }

    /**
     * 添加RES资源图片(本地图片)
     *
     * @param resIds 图片资源ID
     */
    public void addImageRes(ArrayList<Integer> resIds) {
        if (resIds.size() > 6 || resIds.size() < 2) throw new RuntimeException("图片最少2张且不超过6张");
        addImageRes(resIds, null);
    }

    /**
     * 添加RES资源图片(本地图片)
     *
     * @param resIds    图片资源ID
     * @param scaleType ImageView的ScaleType
     */
    public void addImageRes(ArrayList<Integer> resIds, ImageView.ScaleType scaleType) {
        if (resIds.size() > 6 || resIds.size() < 2) throw new RuntimeException("图片最少2张且不超过6张");

        for (int i = 0; i < resIds.size(); i++) {
            mIndicators2.get(i).setVisibility(VISIBLE);
            mIndicators.add(mIndicators2.get(i));
        }

        resIds.add(0, resIds.get(resIds.size() - 1));
        resIds.add(resIds.size(), resIds.get(1));

        for (Integer resId : resIds) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(resId);
            if (scaleType != null)
                imageView.setScaleType(scaleType);
            mImageViews.add(imageView);
        }

        initBanner();
    }

    /**
     * 添加网络图片或本地图片
     *
     * @param urls        图片URL
     * @param placeholder 加载前图片
     * @param error       加载错误图片
     */
    public void addUrl(ArrayList<String> urls, int placeholder, int error) {
        if (urls.size() > 6 || urls.size() < 2) throw new RuntimeException("图片最少2张且不超过6张");

        addUrl(urls, placeholder, error, null);
    }

    /**
     * 添加网络图片或本地图片
     *
     * @param urls        图片URL
     * @param placeholder 加载前图片
     * @param error       加载错误图片
     * @param scaleType   ImageView的ScaleType
     */
    public void addUrl(ArrayList<String> urls, int placeholder, int error, ImageView.ScaleType scaleType) {
        if (urls.size() > 6 || urls.size() < 2) throw new RuntimeException("图片最少2张且不超过6张");

        for (int i = 0; i < urls.size(); i++) {
            mIndicators2.get(i).setVisibility(VISIBLE);
            mIndicators.add(mIndicators2.get(i));
        }

        urls.add(0, urls.get(urls.size() - 1));
        urls.add(urls.size(), urls.get(1));

        for (int i = 0; i < urls.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            if (scaleType != null)
                imageView.setScaleType(scaleType);

            Glide.with(mContext)
                    .load(urls.get(i))
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
