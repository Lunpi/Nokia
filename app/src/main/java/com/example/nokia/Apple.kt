package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import kotlin.math.min
import kotlin.random.Random

class Apple(context: Context, var x: Int, var y: Int, val size: Int) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.apple)
        style = Paint.Style.FILL_AND_STROKE
    }

    lateinit var bounds: Rect

    fun draw(canvas: Canvas) {
        canvas.drawRect(Rect(x, y, x + size, min(y + size, canvas.height)), paint)
    }

    fun relocate() {
        x = Random.nextInt(bounds.left, bounds.right / unit) * unit
        y = Random.nextInt(bounds.top, bounds.bottom / unit) * unit
    }
}