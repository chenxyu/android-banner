package com.chenxyu.androidbanner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.chenxyu.bannerlibrary.BannerView
import com.chenxyu.bannerlibrary.BannerView2
import com.chenxyu.bannerlibrary.listener.OnItemClickListener

/**
 * @author ChenXingYu
 * @version v1.3.0
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findView()
        init()
    }

    private fun findView() {

    }

    private fun init() {
        // 自定义Adapter
        val mADBannerView = findViewById<BannerView>(R.id.ad_banner_view)
        val mImageUrls = mutableListOf<String?>()
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151718678&di=b0d073ad41f1e125aa7ee4abfcc9e2aa&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1920h1080%2F20180106%2F9692-fyqincu7584307.jpg")
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151462489&di=472f98f77c71a36dc90cde4ced4bb9e9&imgtype=0&src=http%3A%2F%2Fvsd-picture.cdn.bcebos.com%2F4649cd5d6dac13c4ae0901967f988fa691be04a9.jpg")
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151590305&di=09f460cb77e3cee5caae3d638c637abc&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201312%2F27%2F20131227233022_Bd3Ft.jpeg")
        mImageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        val mImageViewAdapter = ImageViewAdapter(this, mImageUrls)
        mADBannerView.setLifecycle(this)
                .setAdapter(mImageViewAdapter)
                .setIndicator()
                .setAutoPlay(true)
                .setDelayMillis(3000L)
                .setDuration(500)
                .build()
        mImageViewAdapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                Toast.makeText(this@MainActivity, position.toString(),
                        Toast.LENGTH_SHORT).show()
            }

        }

        // 自定义Adapter
        val mNewsBannerView = findViewById<BannerView2>(R.id.news_banner_view)
        val mTitles = mutableListOf<String?>()
        mTitles.add("世卫组织发言人：新冠疫情尚未到达顶峰")
        mTitles.add("莫斯科将实施通行证制度")
        mTitles.add("单日新增2972例 意大利累计确诊超16万")
        mTitles.add("美或需保持社交隔离措施至2022年")
        mTitles.add("新冠肺炎康复者能否抵御二次感染?世卫回应")
        val mNewsAdapter = NewsAdapter(mTitles)
        mNewsBannerView.setLifecycle(this)
                .setLayoutManager(this, BannerView2.VERTICAL)
                .setAdapter(mNewsAdapter)
                .setAutoPlay(true)
                .setDelayMillis(3000L)
                .setDuration(500)
                .build()
        mNewsAdapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                Toast.makeText(this@MainActivity, position.toString(),
                        Toast.LENGTH_SHORT).show()
            }

        }
    }

}