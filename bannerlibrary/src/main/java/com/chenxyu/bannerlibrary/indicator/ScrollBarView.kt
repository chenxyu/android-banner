package com.chenxyu.bannerlibrary.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chenxyu.bannerlibrary.extend.dpToPx
import java.math.BigDecimal

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2021/3/27 22:38
 * @Description:   滚动条
 * @Version:       1.0
 */
internal class ScrollBarView : View {
    companion object {
        private const val RADIUS = 5F
    }

    private val mPaint: Paint = Paint()
    private val mRectF = RectF()
    private var mMeasureWidth: Int = 0
    private var mMeasureHeight: Int = 0
    private var mBarWH: Int = 0
    private var mStartX: Int = 0
    private var mStartY: Int = 0
    private var mDx: Float = 0F
    private var mDy: Float = 0F
    private var mMaxWH: Int = 0

    /**
     * 方向
     */
    var orientation: Int = RecyclerView.HORIZONTAL

    /**
     * 滚动条颜色
     */
    var barColor: Int? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 滚动条轨道颜色
     */
    var trackColor: Int? = null
        set(value) {
            field = value
            invalidate()
        }

    init {
        mPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        mMeasureWidth = 0
        mMeasureHeight = 0

        if (widthMode == MeasureSpec.EXACTLY) {
            mMeasureWidth = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            mMeasureWidth = widthSize.coerceAtMost(width)
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mMeasureHeight = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            mMeasureHeight = heightSize.coerceAtMost(height)
        }
        setMeasuredDimension(mMeasureWidth, mMeasureHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        when (orientation) {
            RecyclerView.HORIZONTAL -> {
                mBarWH = mMeasureWidth.div(2)
                // 轨道
                mPaint.apply {
                    color = trackColor ?: Color.WHITE
                }
                mRectF.set(0F, 0F, mMeasureWidth.toFloat(), mMeasureHeight.toFloat())
                canvas?.drawRoundRect(mRectF, RADIUS.dpToPx(context), RADIUS.dpToPx(context), mPaint)
                // 滚动条
                mPaint.apply {
                    color = barColor ?: Color.BLUE
                }
                mRectF.set(mDx, 0F, mBarWH + mDx, mMeasureHeight.toFloat())
                canvas?.drawRoundRect(mRectF, RADIUS.dpToPx(context), RADIUS.dpToPx(context), mPaint)
            }
            RecyclerView.VERTICAL -> {
                mBarWH = mMeasureHeight.div(2)
                // 轨道
                mPaint.apply {
                    color = trackColor ?: Color.WHITE
                }
                mRectF.set(0F, 0F, mMeasureWidth.toFloat(), mMeasureHeight.toFloat())
                canvas?.drawRoundRect(mRectF, RADIUS.dpToPx(context), RADIUS.dpToPx(context), mPaint)
                // 滚动条
                mPaint.apply {
                    color = barColor ?: Color.BLUE
                }
                mRectF.set(0F, mDy, mMeasureWidth.toFloat(), mBarWH + mDy)
                canvas?.drawRoundRect(mRectF, RADIUS.dpToPx(context), RADIUS.dpToPx(context), mPaint)
            }
        }
    }

    fun scroll(maxWH: Int, dx: Int, dy: Int) {
        if (maxWH != 0) {
            when (orientation) {
                RecyclerView.HORIZONTAL -> {
                    mMaxWH = maxWH
                    val b1 = BigDecimal(mMeasureWidth - mBarWH)
                    val b2 = BigDecimal(mMaxWH)
                    val b3 = BigDecimal(mStartX + dx)
                    mDx = b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP).times(b3).toFloat()
                    mStartX += dx
                }
                RecyclerView.VERTICAL -> {
                    mMaxWH = maxWH
                    val b1 = BigDecimal(mMeasureHeight - mBarWH)
                    val b2 = BigDecimal(mMaxWH)
                    val b3 = BigDecimal(mStartY + dy)
                    mDy = b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP).times(b3).toFloat()
                    mStartY += dy
                }
            }
            invalidate()
        }
    }

}