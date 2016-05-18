package com.chenxyu.androidbanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.chenxyu.bannerlibrary.BannerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BannerView mBannerView;
    private ArrayList<String> mImagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        init();
    }

    private void findView() {
        mBannerView = (BannerView) findViewById(R.id.ad_view);
    }

    private void init() {
        // 固定5张图片不能少不能多
        mImagePaths.add("http://a.hiphotos.baidu.com/image/pic/item/3bf33a87e950352ad6465dad5143fbf2b2118b6b.jpg");
        mImagePaths.add("http://a.hiphotos.baidu.com/image/pic/item/c8177f3e6709c93d002077529d3df8dcd0005440.jpg");
        mImagePaths.add("http://f.hiphotos.baidu.com/image/pic/item/7aec54e736d12f2ecc3d90f84dc2d56285356869.jpg");
        mImagePaths.add("http://c.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec5ed8456c2d2eb9389b506b38.jpg");
        mImagePaths.add("http://e.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de308a87fc96eef01f3a297969.jpg");
        mBannerView.addUrl(mImagePaths, R.mipmap.ic_launcher, R.mipmap.ic_launcher, ImageView.ScaleType.CENTER_CROP);

        mBannerView.addOnItemClickListener(new BannerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // position对应12345
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}