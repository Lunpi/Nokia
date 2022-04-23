package com.example.nokia

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.ceil
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val unit = resources.getDimension(R.dimen.unit_size).toInt()
    private val dp = resources.displayMetrics.density

    private val bg = ContextCompat.getDrawable(context, R.drawable.bg_woods)
    private val shakeAnimator = ValueAnimator.ofFloat(0f, Random.nextInt(-4, 4) * dp).apply {
        interpolator = DecelerateInterpolator()
        duration = 20
        repeatCount = 5
        addListener(
            onRepeat = {
                (it as ValueAnimator).setFloatValues(0f, Random.nextInt(-4, 4) * dp)
                translationX = it.animatedValue as Float
                translationY = it.animatedValue as Float
            },
            onEnd = {
                translationX = 0f
                translationY = 0f
            }
        )
    }

    val snake = Snake(context, unit * 3, unit * 3, unit)
    lateinit var apple: Apple

    init {
        Timer().scheduleAtFixedRate(timerTask {
            invalidate()
        }, 0, 200)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        apple = Apple(
            context,
            Random.nextInt(0, w / unit) * unit,
            Random.nextInt(0, h / unit) * unit,
            unit
        )
        bg?.setBounds(
            left - ceil(dp * 4).toInt(),
            top - ceil(dp * 4).toInt(),
            right + ceil(dp * 4).toInt(),
            bottom + ceil(dp * 4).toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bg?.draw(canvas)

        snake.move(width, height)

        // check collision
        when (collisionWith()) {
            is Apple -> {
                shakeAnimator.start()
                snake.grow()
                apple.relocate(width, height)
            }
            is Snake -> {
                snake.status = Snake.STATUS_DEAD
            }
            else -> {
                apple.draw(canvas)
            }
        }

        snake.draw(canvas)
    }

    private fun collisionWith(): Any? {
        return when {
            snake.x == apple.x && snake.y == apple.y -> apple
            snake.body.subList(0, snake.body.size - 1).contains(snake.body.last()) -> snake
            else -> null
        }
    }
}