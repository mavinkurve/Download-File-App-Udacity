package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Dimensions
    private var widthSize = 0
    private var heightSize = 0

    // Attribute values
    private var textColor = 0
    private var btnBackgroundColor = 0
    private var progressColor = 0
    private var circleColor = 0

    // Animations
    private var progressAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    // Animation values
    private var progress = 0.0f
    private var startAngle = 270f
    private var sweepAngle = 0f

    private var buttonText: String

    /**
     * Stop animation when download is completed
     */
    private fun ValueAnimator.endAnimation() {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator?) {
                if (buttonState == ButtonState.Completed) {
                    buttonText = context.getString(R.string.download_button_text)
                    sweepAngle = 0f
                    progress = 0f
                    end()
                }
            }
        })
    }

    /**
     * Observe [ButtonState] changes to animate accordingly
     */
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed)
    { property, old, new ->

        when (new) {
            ButtonState.Clicked -> {
                buttonText = context.getString(R.string.download_button_text)
                progress = 0f
                sweepAngle = 0f
            }
            ButtonState.Loading -> {
                // Loading text
                buttonText = context.getString(R.string.button_loading)

                // Progress animator
                progressAnimator = ValueAnimator
                    .ofFloat(0f, measuredWidth.toFloat())
                    .setDuration(3000)
                    .apply {
                    addUpdateListener { valueAnimator ->
                        progress = valueAnimator.animatedValue as Float
                        repeatCount = ValueAnimator.INFINITE
                        invalidate()
                    }
                }
                progressAnimator.endAnimation()
                progressAnimator.start()

                // Circle animator
                circleAnimator = ValueAnimator
                    .ofFloat(0f, 360f)
                    .setDuration(3000)
                    .apply {
                    addUpdateListener { valueAnimator ->
                        sweepAngle = valueAnimator.animatedValue as Float
                        repeatCount = ValueAnimator.INFINITE
                        invalidate()
                    }
                }
                circleAnimator.endAnimation()
                circleAnimator.start()
            }
            ButtonState.Completed -> {
                progress = 0f
                sweepAngle = 0f
                invalidate()
            }
        }
    }


    init {
        isClickable = true
        buttonText = context.getString(R.string.download_button_text)

        // Assign custom attributes to LoadingButton
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            try {
                textColor = getColor(
                    R.styleable.LoadingButton_textColor,
                    ContextCompat.getColor(context, R.color.secondaryTextColor))
                btnBackgroundColor = getColor(
                    R.styleable.LoadingButton_backgroundColor,
                    ContextCompat.getColor(context, R.color.secondaryColor))
                progressColor = getColor(
                    R.styleable.LoadingButton_progressBarColor,
                    ContextCompat.getColor(context, R.color.secondaryDarkColor))
                circleColor = getColor(
                    R.styleable.LoadingButton_circleColor,
                    ContextCompat.getColor(context, R.color.primaryColor))
            }
            finally {
                recycle()
            }
        }
    }

    // Background paint
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = btnBackgroundColor
    }

    // Text paint
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = textColor
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    // Progress paint
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = progressColor
    }

    // Circle paint
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = circleColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), buttonPaint)

        drawProgress(canvas)

        val textOffset = (textPaint.descent() - textPaint.ascent()) / 2 - textPaint.descent()
        val textCenterX = measuredWidth.toFloat() / 2
        val textCenterY = measuredHeight.toFloat() / 2 + textOffset
        canvas?.drawText(buttonText, textCenterX, textCenterY, textPaint)

        drawCircle(canvas)
    }

    /**
     * Called to draw loading rectangle
     * @param canvas: [Canvas] to draw on
     */
    private fun drawProgress(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, progress, measuredHeight.toFloat(), progressPaint)
    }

    /**
     * Called to draw loading circle
     * @param canvas: [Canvas] to draw on
     */
    private fun drawCircle(canvas: Canvas?) {

        val circleLeft = measuredWidth.toFloat() - 250f
        val circleTop = 70f

        canvas?.drawArc(circleLeft, circleTop,
            circleLeft + 70f, circleTop + 70f,
            startAngle, sweepAngle, true, circlePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    /**
     * Called to change [ButtonState] of [LoadingButton]
     */
    fun onButtonStateChanged(state: ButtonState) {
        buttonState = state
    }

}