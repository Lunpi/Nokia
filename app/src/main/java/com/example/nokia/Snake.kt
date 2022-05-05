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
import com.example.nokia.GameUtils.Companion.STATUS_INVINCIBLE
import kotlin.math.min

class Snake(context: Context, var x: Int, var y: Int, val size: Int) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    // prevent clicking the turn button rapidly which causes direction changes multiple times
    private var directionLock = false

    private val colorTransparent = ContextCompat.getColor(context, R.color.transparent)
    private val colorBlack = ContextCompat.getColor(context, R.color.black)
    private val paint = Paint().apply {
        color = colorBlack
        style = Paint.Style.FILL_AND_STROKE
    }
    private var invincibleCountDownTimer = 0

    val body = ArrayList<SnakeBody>().apply {
        // initial length: 3
        for (i in 2 downTo 0) {
            add(SnakeBody(x - (i * unit), y))
        }
    }

    // to decide where should the snake grows
    private val tail = SnakeBody(body.first().x - unit, y)

    lateinit var bounds: Rect
    var direction = DIRECTION_RIGHT
    var status = STATUS_ALIVE

    fun draw(canvas: Canvas) {
        paint.color = when (status) {
            STATUS_DEAD, STATUS_INVINCIBLE -> if (paint.color == colorBlack) colorTransparent else colorBlack
            else -> colorBlack
        }
        body.forEach {
            canvas.drawRect(Rect(it.x, it.y, it.x + size, min(it.y + size, canvas.height)), paint)
        }
    }

    fun move() {
        if (status == STATUS_DEAD) {
            return
        }
        x = when (direction) {
            DIRECTION_LEFT -> if (x <= bounds.left) bounds.right - (bounds.right % unit) else x - unit
            DIRECTION_RIGHT -> if (x + unit >= bounds.right) bounds.left else x + unit
            else -> x
        }
        y = when (direction) {
            DIRECTION_UP -> if (y <= bounds.top) bounds.bottom - (bounds.bottom % unit) else y - unit
            DIRECTION_DOWN -> if (y + unit >= bounds.bottom) bounds.top else y + unit
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

    fun update() {
        if (status == STATUS_INVINCIBLE) {
            if (invincibleCountDownTimer > 0) {
                invincibleCountDownTimer--
            } else {
                status = STATUS_ALIVE
            }
        }
    }

    fun invincible(time: Int) {
        status = STATUS_INVINCIBLE
        invincibleCountDownTimer = time
    }
}

data class SnakeBody(var x: Int, var y: Int)