package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import com.example.nokia.GameUtils.Companion.DIRECTION_DOWN
import com.example.nokia.GameUtils.Companion.DIRECTION_LEFT
import com.example.nokia.GameUtils.Companion.DIRECTION_RIGHT
import com.example.nokia.GameUtils.Companion.DIRECTION_STILL
import com.example.nokia.GameUtils.Companion.DIRECTION_UP
import com.example.nokia.GameUtils.Companion.STATUS_ALIVE
import com.example.nokia.GameUtils.Companion.STATUS_INVINCIBLE
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

class Enemy(context: Context, var x: Int, var y: Int, val size: Int) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    private val colorTransparent = ContextCompat.getColor(context, R.color.transparent)
    private val colorBlue = ContextCompat.getColor(context, R.color.purple_500)
    private val paint = Paint().apply {
        color = colorBlue
        style = Paint.Style.FILL_AND_STROKE
    }

    private var invincibleCountDownTimer = 0
    private var moveCountDownTimer = 0
    private var direction = DIRECTION_STILL
    private var moveSpeed = .5f

    lateinit var bounds: Rect
    var status = STATUS_ALIVE

    private fun setMovement(direction: Int, time: Int) {
        this.direction = direction
        moveCountDownTimer = time
    }

    private fun move(direction: Int, speed: Float) {
        val distance = (speed * unit).toInt()
        x = when (direction) {
            DIRECTION_LEFT -> max(bounds.left, x - distance)
            DIRECTION_RIGHT -> min(bounds.right - size, x + distance)
            else -> x
        }
        y = when (direction) {
            DIRECTION_UP -> max(bounds.top, y - distance)
            DIRECTION_DOWN -> min(bounds.bottom - size, y + distance)
            else -> y
        }
    }

    fun draw(canvas: Canvas) {
        paint.color = when (status) {
            STATUS_INVINCIBLE -> if (paint.color == colorBlue) colorTransparent else colorBlue
            else -> colorBlue
        }
        canvas.drawRect(Rect(x, y, x + size, min(y + size, canvas.height)), paint)
    }

    fun action() {
        if (status == STATUS_INVINCIBLE) {
            if (invincibleCountDownTimer > 0) {
                invincibleCountDownTimer--
            } else {
                status = STATUS_ALIVE
            }
        }
        if (moveCountDownTimer > 0) {
            move(direction, moveSpeed)
            moveCountDownTimer--
        } else {
            setMovement(Random.nextInt(DIRECTION_DOWN..DIRECTION_UP), 6)
        }
    }

    fun knockoff(direction: Int, strength: Float) {
        move(direction, strength)
        setMovement(DIRECTION_STILL, 3)
    }

    fun invincible(time: Int) {
        status = STATUS_INVINCIBLE
        invincibleCountDownTimer = time
    }
}