package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import kotlin.random.Random

class Apple(context: Context, var x: Int, var y: Int, private val size: Int) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.apple)
        style = Paint.Style.FILL_AND_STROKE
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(Rect(x, y, x + size, y + size), paint)
    }

    fun relocate(boundX: Int, boundY: Int) {
        x = Random.nextInt(0, boundX / unit) * unit
        y = Random.nextInt(0, boundY / unit) * unit
    }
}