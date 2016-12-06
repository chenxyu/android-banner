package com.chenxyu.bannerlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ChenXingYu on 16/5/16.
 */
public class BannerViewPager extends ViewPager {
    private boolean isTouch = false;
    private int mTime = 5000;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isTouch) {
                switch (getCurrentItem()) {
                    case 0:
                        setCurrentItem(1);
                        break;
                    case 1:
                        setCurrentItem(2);
                        break;
                    case 2:
                        setCurrentItem(3);
                        break;
                    case 3:
                        setCurrentItem(4);
                        break;
                    case 4:
                        setCurrentItem(5);
                        break;
                    case 5:
                        setCurrentItem(6);
                        break;
                    case 6:
                        setCurrentItem(7);
                        break;
                }
            }
            mHandler.sendEmptyMessageDelayed(0, mTime);
        }
    };

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler.sendEmptyMessageDelayed(0, mTime);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                isTouch = true;
                break;

            case MotionEvent.ACTION_UP:
                isTouch = false;
                mHandler.removeMessages(0);
                mHandler.sendEmptyMessageDelayed(0, mTime);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setTimeDelayed(int time) {
        this.mTime = time;
    }
}
