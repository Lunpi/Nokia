package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat

class Projectile(
    context: Context,
    var x: Int,
    var y: Int,
    val size: Int,
    private val direction: Int,
    private val speed: Float
) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.projectile)
        style = Paint.Style.FILL_AND_STROKE
    }

    fun move() {
        val distance = (speed * unit).toInt()
        x = when (direction) {
            GameUtils.DIRECTION_LEFT -> x - distance
            GameUtils.DIRECTION_RIGHT -> x + distance
            else -> x
        }
        y = when (direction) {
            GameUtils.DIRECTION_UP -> y - distance
            GameUtils.DIRECTION_DOWN -> y + distance
            else -> y
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), size / 2f, paint)
    }
}