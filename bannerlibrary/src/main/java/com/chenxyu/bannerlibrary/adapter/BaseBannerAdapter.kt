package com.chenxyu.bannerlibrary.adapter

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView
import com.chenxyu.bannerlibrary.listener.OnItemClickListener
import com.chenxyu.bannerlibrary.listener.OnItemLongClickListener

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/14 22:47
 * @Description:   自定义Adapter需要继承此类,使用getReal开头的方法获取真实的数据
 * @Version:       1.0
 * @param VH ViewHolder
 * @param T 数据类型
 */
abstract class BaseBannerAdapter<VH : RecyclerView.ViewHolder, T>(
        private val mData: MutableList<T?>
) : RecyclerView.Adapter<VH>() {
    private val transformData = mutableListOf<T?>()
    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null

    init {
        transformData.addAll(mData)
        if (transformData.size < 1) throw RuntimeException("Minimum size 1")
        transformData.add(0, this.transformData[this.transformData.size - 1])
        transformData.add(this.transformData.size, this.transformData[1])
    }

    /**
     * 处理过的ItemCount
     */
    override fun getItemCount(): Int = transformData.size

    /**
     * 处理过的Data
     */
    fun getData(): MutableList<T?> {
        return transformData
    }

    /**
     * 真实的ItemCount
     */
    fun getRealItemCount(): Int = mData.size

    /**
     * 真实的Data
     */
    fun getRealData(): MutableList<T?> {
        return mData
    }

    /**
     * 真实的Position
     * @param position [onBindViewHolder]里面的position
     */
    fun getRealPosition(position: Int): Int {
        return when (position) {
            0 -> mData.size - 1
            transformData.size - 1 -> 0
            else -> position - 1
        }
    }

    /**
     * 真实的Item
     * @param position [onBindViewHolder] [OnItemClickListener] [OnItemLongClickListener]里面的position
     */
    fun getRealItem(position: Int): T? {
        return when (position) {
            0 -> mData[mData.size - 1]
            transformData.size - 1 -> mData[0]
            else -> mData[position - 1]
        }
    }

    /**
     * 根布局强制MATCH_PARENT
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val bannerViewHolder = onCreateVH(parent, viewType)
        bannerViewHolder.itemView.rootView.apply {
            if (this.layoutParams == null || this.layoutParams.width != MATCH_PARENT ||
                    this.layoutParams.height != MATCH_PARENT) {
                val layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                this.layoutParams = layoutParams
            }
        }
        return bannerViewHolder
    }

    /**
     * @param parent 绑定到适配器位置后，新视图将被添加到其中的ViewGroup
     * @param viewType 视图类型
     */
    abstract fun onCreateVH(parent: ViewGroup, viewType: Int): VH

    /**
     * ClickListener获取的都是真实position
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(it, getRealPosition(position))
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(it, getRealPosition(position))
            return@setOnLongClickListener true
        }
        onBindViewHolder(holder, position, transformData[position])
    }

    /**
     * @param holder ViewHolder
     * @param position 当前Item位置
     * @param item 当前Item数据
     */
    abstract fun onBindViewHolder(holder: VH, position: Int, item: T?)
}