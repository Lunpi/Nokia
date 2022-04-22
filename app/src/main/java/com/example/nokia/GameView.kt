package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val unit = resources.getDimension(R.dimen.unit_size).toInt()

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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        snake.move(width, height)

        // check collision
        when (collisionWith()) {
            is Apple -> {
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