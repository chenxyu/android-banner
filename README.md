[![](https://jitpack.io/v/chenxyu/android-banner.svg)](https://jitpack.io/#chenxyu/android-banner)

# android-banner
为了支持AndroidX使用Kotlin重构，滑动改用 `ViewPager2` ，自带4种动画，支持自定义Adapter（继承 `BaseBannerAdapter` ）和动画，支持自定义指示器位置大小颜色等。  
使用AndroidX的 `Activity` 或 `Fragment` 都实现了 `LifecycleOwner` 接口，只需传入当前 `Lifecycle` 会根据当前生命周期管理 Banner开始和暂停。

BannerView（基于ViewPager2）：支持动画。  
BannerView2（基于RecyclerView）：不支持动画，isAutoPlay值null时可以设置ItemView的Margin（不循环）。
Indicator：指示器可以自定义，支持ViewPager2和RecyclerView。

![示例](https://img-blog.csdnimg.cn/20200416104537970.gif#pic_center)

![示例](https://img-blog.csdnimg.cn/20210331023708420.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzQ2OTU4NTg0,size_16,color_FFFFFF,t_70#pic_center)

# Gradle 依赖

1.root build.gradle

```kotlin
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

2.app build.gradle

```kotlin
dependencies {
	implementation 'com.github.chenxyu:android-banner:2.6.2'
}
```

# 使用方法

```kotlin
    <com.chenxyu.bannerlibrary.BannerView
        android:id="@+id/ad_banner_view"
        android:layout_width="match_parent"
        android:layout_height="200dp" />

    <com.chenxyu.bannerlibrary.BannerView
        android:id="@+id/news_banner_view"
        android:layout_width="match_parent"
        android:layout_height="50dp" />

        // BannerView
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

        // BannerView2
        val mImageUrls2 = mutableListOf<String?>()
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151718678&di=b0d073ad41f1e125aa7ee4abfcc9e2aa&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1920h1080%2F20180106%2F9692-fyqincu7584307.jpg")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151462489&di=472f98f77c71a36dc90cde4ced4bb9e9&imgtype=0&src=http%3A%2F%2Fvsd-picture.cdn.bcebos.com%2F4649cd5d6dac13c4ae0901967f988fa691be04a9.jpg")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151590305&di=09f460cb77e3cee5caae3d638c637abc&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201312%2F27%2F20131227233022_Bd3Ft.jpeg")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        mImageUrls2.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583151690450&di=c33be331339fbc65459864f802fa1cc7&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw1142h639%2F20180203%2F9979-fyrcsrx2995071.png")
        // BannerView2
        val mADBannerView2 = findViewById<BannerView2>(R.id.ad_banner_view2)
        val mImageViewAdapter2 = ImageViewAdapter2(this, mImageUrls2)
        mADBannerView2.setLifecycle(this)
                .setOrientation(BannerView2.HORIZONTAL)
                .setGridLayoutManager(GridLayoutManager(this, 2))
                .setAdapter(mImageViewAdapter2, BannerView2.Margins(10, 16, 10, 0))
                .setIndicator(ScrollIndicator(false))
                .setShowCount(3)
                .setDelayMillis(3000L)
                .setDuration(500)
                .build()
        mImageViewAdapter2.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                Toast.makeText(this@MainActivity, position.toString(),
                        Toast.LENGTH_SHORT).show()
            }
        }

        // BannerView2
        val mNewsBannerView = findViewById<BannerView2>(R.id.news_banner_view)
        val mTitles = mutableListOf<String?>()
        mTitles.add("世卫组织发言人：新冠疫情尚未到达顶峰")
        mTitles.add("莫斯科将实施通行证制度")
        mTitles.add("单日新增2972例 意大利累计确诊超16万")
        mTitles.add("美或需保持社交隔离措施至2022年")
        mTitles.add("新冠肺炎康复者能否抵御二次感染?世卫回应")
        val mNewsAdapter = NewsAdapter(mTitles)
        mNewsBannerView.setLifecycle(this)
                .setOrientation(BannerView2.VERTICAL)
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
```
`BaseBannerAdapter` 支持 `OnItemClickListener` 和 `OnItemLongClickListener`，通过ClickListener获取的 `position` 都是真实的。在自定义 `Adapter` 里使用 `getItemCount` 和 `getData` 获取数据，如果需要真实位置和数据需要使用 `getReal` 开头的方法获取，每个方法都有注释。

```kotlin
class ImageViewAdapter(private val mContext: Context?, mImages: MutableList<String?>)
    : BannerView.Adapter<ImageViewAdapter.ImageViewHolder, String>(mImages) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(mContext)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int, item: String?) {
        holder.initView(item, position, mContext)
    }

    class ImageViewHolder(itemView: View) : BannerView.ViewHolder<String>(itemView) {

        override fun initView(item: String?, position: Int?, context: Context?) {
            context?.let {
                Glide.with(it)
                        .load(item)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .transition(withCrossFade())
                        .into(itemView as ImageView)
            }
        }
    }
}

class NewsAdapter(data: MutableList<String?>) :
        BannerView2.Adapter<NewsAdapter.TextViewHolder, String>(data) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_text_news, parent, false))
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int, item: String?) {
        holder.initView(item)
    }

    class TextViewHolder(itemView: View) : BannerView2.ViewHolder<String>(itemView) {

        override fun initView(item: String?, position: Int?, context: Context?) {
            val tvNews = itemView.findViewById<TextView>(R.id.tv_news)
            item?.let { tvNews.text = it }
        }
    }

}
```

# 设置方法属性


| 方法名（返回this） | 说明 |
|--|--|
| setLifecycle | 观察Fragment或Activity生命周期控制Banner开始和暂停 |
| setAdapter | 自定义Adapter（继承BaseBannerAdapter），滑动方向 |
| setAutoPlay | 自动循环轮播 |
| setOffscreenPageLimit | 预加载页面限制（BannerView） |
| setDelayMillis | 页面切换延迟时间 |
| setDuration | 滑动持续时间 |
| setRoundRect | Banner圆角 |
| setShowCount | 显示一屏显示个数（BannerView2） |
| setGridLayoutManager | 网格布局管理（BannerView2） |
| setPageMargin | 设置页面间距（BannerView） |
| setMultiPage | 一屏多页 ，在[setAdapter]之后设置（BannerView） |
| setScalePageTransformer | 缩放动画，在[setAdapter]之后设置（BannerView） |
| setZoomOutPageTransformer | 官方示例缩放动画，在[setAdapter]之后设置（BannerView） |
| setRotationPageTransformer | 官方示例旋转动画，在[setAdapter]之后设置（BannerView） |
| setDepthPageTransformer | 官方示例深度动画，在[setAdapter]之后设置（BannerView） |
| setPageTransformer | 自定义动画（BannerView） |
| build | 创建Banner |

| 方法名 | 说明 |
|--|--|
| start | 开始循环 |
| pause | 暂停循环 |

| XML属性 | 说明 |
|--|--|
| app:orientation | 滑动方向 |
| app:indicatorNormal | 未选中指示器DrawableRes |
| app:indicatorSelected | 选中指示器DrawableRes |
| app:indicatorMargin | 指示器Margin |
| app:indicatorGravity | 设置指示器位置 |
| app:autoPlay | 自动循环轮播（BannerView） |
| app:offscreenPageLimit | 预加载页面限制 |
| app:delayMillis | 页面切换延迟时间 |
| app:duration | 滑动持续时间 |

