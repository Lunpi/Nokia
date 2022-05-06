package com.example.nokia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

class HealthBar(context: Context, var capacity: Int, var hp: Int, val color: Int) {

    private val leftHalf =
        ContextCompat.getDrawable(context, R.drawable.ic_heart_left_half_24dp)?.apply {
            setTint(color)
        }
    private val rightHalf =
        ContextCompat.getDrawable(context, R.drawable.ic_heart_right_half_24dp)?.apply {
            setTint(color)
        }
    val heartSize = leftHalf?.intrinsicWidth ?: 0

    fun draw(canvas: Canvas, x: Int, y: Int) {
        for (i in 1..hp) {
            val heart = if (i % 2 == 0) rightHalf else leftHalf
            heart?.run {
                val left = x + (heartSize * (i - 1) / 2)
                bounds = Rect(left, y, left + heartSize, y + heartSize)
                draw(canvas)
            }
        }
    }

    fun decrease(damage: Int) {
        hp = max(0, hp - damage)
    }

    fun increase(heal: Int) {
        hp = min(capacity, hp + heal)
    }
}