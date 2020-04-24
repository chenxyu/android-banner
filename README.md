[![](https://jitpack.io/v/chenxyu/android-banner.svg)](https://jitpack.io/#chenxyu/android-banner)

# android-banner
为了支持AndroidX使用Kotlin重构，滑动改用 `ViewPager2` ，自带图片轮播Adapter（图片加载依赖: `Glide 4.11.0` ）和4种动画，支持自定义Adapter（继承 `BaseBannerAdapter` ）和动画，支持自定义指示器位置大小颜色等。
使用AndroidX的 `Activity` 或 `Fragment` 都实现了 `LifecycleOwner` 接口，只需传入当前 `Lifecycle` 会根据当前生命周期管理 Banner开始和暂停。

![示例](https://img-blog.csdnimg.cn/20200416104537970.gif#pic_center)

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
	implementation 'com.github.chenxyu:android-banner:2.2.0'
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

        // 简单使用
        mADBannerView.setLifecycle(this)
                .setUrls(mImageUrls)
                .setPlaceholder(R.mipmap.ic_launcher)
                .setError(R.mipmap.ic_launcher)
                .setScaleType(ImageView.ScaleType.CENTER_CROP)
                .setOrientation(BannerView.HORIZONTAL)
                .setMultiPage(20)
                .setScalePageTransformer()
                .setOnItemClickListener(this)
                .build()

        // 自定义Adapter
        mNewsBannerView.setLifecycle(this)
                .setAdapter(mNewsAdapter)
                .setIndicatorVisibility(View.GONE)
                .setOrientation(BannerView.VERTICAL)
                .build()
        mNewsAdapter.onItemClickListener = this
    }

    override fun onItemClick(view: View?, position: Int) {
        Toast.makeText(this@MainActivity, position.toString(),
                Toast.LENGTH_SHORT).show()
    }
```
`BaseBannerAdapter` 支持 `OnItemClickListener` 和 `OnItemLongClickListener`，通过ClickListener获取的 `position` 都是真实的。在自定义 `Adapter` 里使用 `getItemCount` 和 `getData` 获取数据，如果需要真实位置和数据需要使用 `getReal` 开头的方法获取，每个方法都有注释。

```kotlin
/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/15 9:46
 * @Description:
 * @Version:       1.0
 */
class NewsAdapter(data: MutableList<String?>) :
        BaseBannerAdapter<NewsAdapter.TextViewHolder, String>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_text_news, parent, false))
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int, item: String?) {
        holder.initView(item)
    }

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun initView(item: String?) {
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
| setAdapter | 自定义Adapter（继承BaseBannerAdapter） |
| isLoopViews | 是否循环 |
| setIndicatorUnselected | 未选中指示器DrawableRes |
| setIndicatorSelected | 选中指示器DrawableRes |
| setIndicatorWH | 指示器宽高 |
| setIndicatorMargin | 指示器Margin |
| setDelayMillis | 页面切换时间 |
| setIndicatorVisibility | 指示器显示或隐藏 |
| setIndicatorGravity | 设置指示器位置 |
| setPageMargin | 设置页面间距 |
| setOrientation | 滑动方向 |
| setMultiPage | 一屏多页 ，在[setOrientation]之后设置 |
| setScalePageTransformer | 缩放动画，在[setOrientation]之后设置 |
| setZoomOutPageTransformer | 官方示例缩放动画，在[setOrientation]之后设置 |
| setRotationPageTransformer | 官方示例旋转动画，在[setOrientation]之后设置 |
| setDepthPageTransformer | 官方示例深度动画，在[setOrientation]之后设置 |
| setPageTransformer | 自定义动画 |
| setPlaceholder | 占位符 |
| setError | 错误时显示图片 |
| setScaleType | 图片缩放类型 |
| setResIds | 添加RES资源图片 |
| setUrls | 添加网络图片 |
| setOnItemClickListener | 添加一个Item点击事件 |
| build | 开始构建Banner |

| 方法名 | 说明 |
|--|--|
| start | 开始循环 |
| pause | 暂停循环 |

| XML属性 | 说明 |
|--|--|
| app:orientation | 滑动方向 |
| app:indicatorUnselected | 未选中指示器DrawableRes |
| app:indicatorSelected | 选中指示器DrawableRes |
| app:indicatorWH | 指示器宽高 |
| app:indicatorMargin | 指示器Margin |
| app:indicatorGravity | 设置指示器位置 |
| app:indicatorVisibility | 指示器显示或隐藏 |
| app:loopViews | 是否循环 |
| app:placeholderDrawable | 占位符 |
| app:errorDrawable | 错误时显示图片 |

