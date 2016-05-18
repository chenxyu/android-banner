[![](https://jitpack.io/v/chenxyu/android-banner.svg)](https://jitpack.io/#chenxyu/android-banner)

# android-banner
支持无限轮播,自定义切换速度和时间,指示器位置,加载网络或本地图片.BannerView下查看具体方法.
图片加载依赖:Glide


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
	        compile 'com.github.chenxyu:android-banner:v1.2.0'
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
    mBannerView.addUrl(mImagePaths, R.mipmap.ic_launcher, R.mipmap.ic_launcher, ImageView.ScaleType.CENTER_CROP);
```

```java
    mBannerView.addOnItemClickListener(new BannerView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                }
            });
```