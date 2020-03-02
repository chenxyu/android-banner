package com.chenxyu.androidbanner;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.chenxyu.bannerlibrary.BannerView;

import java.util.ArrayList;

/**
 * @author ChenXingYu
 * @version v1.2.0
 */
public class MainActivity extends FragmentActivity {
    private BannerView mBannerView;
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private ArrayList<Integer> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        init();
    }

    private void findView() {
        mBannerView = findViewById(R.id.banner_view);
    }

    private void init() {
        // 网络或本地图片
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151718678&di=b0d073ad41f1e125aa7ee4abfcc9e2aa&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1920h1080%2F20180106%2F9692-fyqincu7584307.jpg");
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151462489&di=472f98f77c71a36dc90cde4ced4bb9e9&imgtype=0&src=http%3A%2F%2Fvsd-picture.cdn.bcebos.com%2F4649cd5d6dac13c4ae0901967f988fa691be04a9.jpg");
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151524641&di=38eb2dba3249d56e4a5466eb398bb443&imgtype=0&src=http%3A%2F%2Fwww.17qq.com%2Fimg_qqtouxiang%2F74875318.jpeg");
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151590305&di=09f460cb77e3cee5caae3d638c637abc&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201312%2F27%2F20131227233022_Bd3Ft.jpeg");
        mImageUrls.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=404341803,968061960&fm=11&gp=0.jpg");
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png");
        mBannerView.addUrl(mImageUrls, R.mipmap.ic_launcher, R.mipmap.ic_launcher, ImageView.ScaleType.CENTER_CROP);

        // res资源图片
//        mImages.add(R.mipmap.aaa);
//        mImages.add(R.mipmap.aaa);
//        mImages.add(R.mipmap.aaa);
//        mImages.add(R.mipmap.aaa);
//        mImages.add(R.mipmap.aaa);
//        mBannerView.addImageRes(mImages, ImageView.ScaleType.CENTER_CROP);

        mBannerView.addOnItemClickListener(new BannerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBannerView.onResume();
    }

    @Override
    protected void onPause() {
        mBannerView.onPause();
        super.onPause();
    }
}
