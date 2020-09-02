package com.chenxyu.androidbanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chenxyu.bannerlibrary.BannerView2

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/15 9:46
 * @Description:
 * @Version:       1.0
 */
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