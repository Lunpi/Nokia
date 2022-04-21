package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat

class Snake(context: Context, var x: Int, var y: Int, private val size: Int) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    private var direction = DIRECTION_RIGHT

    // prevent clicking the turn button rapidly which causes direction changes multiple times
    private var directionLock = false

    private val body = ArrayList<SnakeBody>().apply {
        add(SnakeBody(x, y))
    }

    // to decide where should the snake grows
    private val tail = SnakeBody(x - unit, y)

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.FILL_AND_STROKE
    }

    fun draw(canvas: Canvas) {
        body.forEach {
            canvas.drawRect(Rect(it.x, it.y, it.x + size, it.y + size), paint)
        }
    }

    fun move(boundX: Int, boundY: Int) {
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


    companion object {
        const val DIRECTION_LEFT = 1
        const val DIRECTION_UP = 2
        const val DIRECTION_RIGHT = 3
        const val DIRECTION_DOWN = 4
    }
}

data class SnakeBody(var x: Int, var y: Int)