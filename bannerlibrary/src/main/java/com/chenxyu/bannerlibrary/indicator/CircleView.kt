package com.chenxyu.bannerlibrary.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chenxyu.bannerlibrary.extend.dpToPx

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2021/5/16 17:05
 * @Description:   环状指示器（平移）
 * @Version:       1.0
 */
internal class CircleView : View {
    private val mPaint: Paint = Paint()
    private var mMeasureWidth: Int = 0
    private var mMeasureHeight: Int = 0
    private var selectedPosition: Int = 0

    /**
     * 方向
     */
    var orientation: Int = RecyclerView.HORIZONTAL

    /**
     * 数量
     */
    var circleCount: Int = 0

    /**
     * 选中颜色
     */
    var selectedColor: Int = Color.WHITE

    /**
     * 默认颜色
     */
    var normalColor: Int = Color.parseColor("#88FFFFFF")

    /**
     * 圆间隔（DP）
     */
    var spacing: Int = 4

    /**
     * 圆宽（DP）
     */
    var circleWidth: Float = 7F

    /**
     * 圆高（DP）
     */
    var circleHeight: Float = 7F

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
                // 默认圆
                mPaint.color = normalColor
                repeat(circleCount) { i ->
                    canvas?.drawCircle(
                            circleWidth.times(i).plus(spacing.times(i)).plus(
                                    circleWidth.div(2)).dpToPx(context),
                            circleHeight.div(2).dpToPx(context),
                            circleWidth.div(2).dpToPx(context), mPaint)
                }
                // 选中圆
                mPaint.color = selectedColor
                canvas?.drawCircle(
                        circleWidth.times(selectedPosition).plus(spacing.times(selectedPosition)).plus(
                                circleWidth.div(2)).dpToPx(context),
                        circleHeight.div(2).dpToPx(context),
                        circleWidth.div(2).dpToPx(context), mPaint)
            }
            RecyclerView.VERTICAL -> {
                // 默认圆
                mPaint.color = normalColor
                repeat(circleCount) { i ->
                    canvas?.drawCircle(circleWidth.div(2).dpToPx(context),
                            circleHeight.times(i).plus(spacing.times(i)).plus(
                                    circleHeight.div(2)).dpToPx(context),
                            circleHeight.div(2).dpToPx(context), mPaint)
                }
                // 选中圆
                mPaint.color = selectedColor
                canvas?.drawCircle(circleWidth.div(2).dpToPx(context),
                        circleHeight.times(selectedPosition).plus(spacing.times(selectedPosition)).plus(
                                circleHeight.div(2)).dpToPx(context),
                        circleHeight.div(2).dpToPx(context), mPaint)
            }
        }
    }

    fun scrollTo(position: Int) {
        if (circleCount > 0 && position >= 0 && position < circleCount) {
            selectedPosition = position
            invalidate()
        }
    }

}