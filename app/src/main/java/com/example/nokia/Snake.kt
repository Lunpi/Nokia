package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.example.nokia.GameUtils.Companion.DIRECTION_DOWN
import com.example.nokia.GameUtils.Companion.DIRECTION_LEFT
import com.example.nokia.GameUtils.Companion.DIRECTION_RIGHT
import com.example.nokia.GameUtils.Companion.DIRECTION_UP
import com.example.nokia.GameUtils.Companion.STATUS_ALIVE
import com.example.nokia.GameUtils.Companion.STATUS_DEAD
import kotlin.math.min

class Snake(private val context: Context, var x: Int, var y: Int, val size: Int) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    // prevent clicking the turn button rapidly which causes direction changes multiple times
    private var directionLock = false

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.FILL_AND_STROKE
    }

    val body = ArrayList<SnakeBody>().apply {
        // initial length: 3
        for (i in 2 downTo 0) {
            add(SnakeBody(x - (i * unit), y))
        }
    }

    // to decide where should the snake grows
    private val tail = SnakeBody(body.first().x - unit, y)

    var direction = DIRECTION_RIGHT
    var status = STATUS_ALIVE

    fun draw(canvas: Canvas) {
        if (status == STATUS_DEAD) {
            paint.apply {
                color = ContextCompat.getColor(
                    context,
                    if (color == ContextCompat.getColor(context, R.color.black)) R.color.transparent
                    else R.color.black
                )
            }
        }
        body.forEach {
            canvas.drawRect(Rect(it.x, it.y, it.x + size, min(it.y + size, canvas.height)), paint)
        }
    }

    fun move(boundX: Int, boundY: Int) {
        if (status == STATUS_DEAD) {
            return
        }
        x = when (direction) {
            DIRECTION_LEFT -> if (x <= 0) boundX - (boundX % unit) else x - unit
            DIRECTION_RIGHT -> if (x + unit >= boundX) 0 else x + unit
            else -> x
        }
        y = when (direction) {
            DIRECTION_UP -> if (y <= 0) boundY - (boundY % unit) else y - unit
            DIRECTION_DOWN -> if (y + unit >= boundY) 0 else y + unit
            else -> y
        }

        tail.apply {
            x = body[0].x
            y = body[0].y
        }
        for (i in 0 until body.size - 1) {
            body[i].x = body[i + 1].x
            body[i].y = body[i + 1].y
        }
        body.last().apply {
            x = this@Snake.x
            y = this@Snake.y
        }

        if (directionLock) {
            directionLock = false
        }
    }

    fun turnLeftDown() {
        if (!directionLock) {
            direction = when (direction) {
                DIRECTION_LEFT, DIRECTION_RIGHT -> DIRECTION_DOWN
                DIRECTION_UP, DIRECTION_DOWN -> DIRECTION_LEFT
                else -> direction
            }
            directionLock = true
        }
    }

    fun turnRightUp() {
        if (!directionLock) {
            direction = when (direction) {
                DIRECTION_LEFT, DIRECTION_RIGHT -> DIRECTION_UP
                DIRECTION_UP, DIRECTION_DOWN -> DIRECTION_RIGHT
                else -> direction
            }
            directionLock = true
        }
    }

    fun grow() {
        body.add(0, SnakeBody(tail.x, tail.y))
    }
}

data class SnakeBody(var x: Int, var y: Int)