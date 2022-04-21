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

    val snake = Snake(context, unit, unit, unit)
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
        if (snake.x == apple.x && snake.y == apple.y) {
            snake.grow()
            apple.relocate(width, height)
        } else {
            apple.draw(canvas)
        }

        snake.draw(canvas)
    }
}