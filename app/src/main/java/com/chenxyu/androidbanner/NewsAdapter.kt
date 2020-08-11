package com.chenxyu.androidbanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chenxyu.bannerlibrary.adapter.BaseBannerAdapter

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/15 9:46
 * @Description:
 * @Version:       1.0
 */
class NewsAdapter(data: MutableList<String?>) :
        BaseBannerAdapter<NewsAdapter.TextViewHolder, String>(data) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): TextViewHolder {
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