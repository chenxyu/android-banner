[![](https://jitpack.io/v/chenxyu/android-banner.svg)](https://jitpack.io/#chenxyu/android-banner)

# android-banner
Kotlin重构项目,AndroidX,ViewPage2.
支持无限轮播,一页多屏,缩放动画,指示器位置,加载网络或本地图片,自定义Glide RequestOptions.
根据Fragment或Activity生命周期控制Banner开始和暂停.

图片加载依赖:Glide 4.11.0


# Gradle 依赖

1.root build.gradle

```java
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```

2.app build.gradle

```java
dependencies {
	        compile 'com.github.chenxyu:android-banner:v2.1.0'
	}
```


# 使用方法

```java
    <com.chenxyu.bannerlibrary.BannerView
        android:id="@+id/banner_view"
        android:layout_width="match_parent"
        android:layout_height="200dp" />
```

```java
    mBannerView.setLifecycle(this)
                    .setPlaceholder(R.mipmap.ic_launcher)
                    .setError(R.mipmap.ic_launcher)
                    .setScaleType(ImageView.ScaleType.CENTER_CROP)
                    .setOnItemClickListener(this)
                    .setUrls(mImageUrls)
                    .build();

    @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(MainActivity.this, String.valueOf(position),
                    Toast.LENGTH_SHORT).show();
        }
```
