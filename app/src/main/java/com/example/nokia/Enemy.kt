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
import com.example.nokia.GameUtils.Companion.STATUS_DEAD
import com.example.nokia.GameUtils.Companion.STATUS_INVINCIBLE
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

class Enemy(private val context: Context, var x: Int, var y: Int, val size: Int) {

    private val unit = context.resources.getDimension(R.dimen.unit_size).toInt()

    private val colorTransparent = ContextCompat.getColor(context, R.color.transparent)
    private val colorBlue = ContextCompat.getColor(context, R.color.purple_500)
    private val colorPurple = ContextCompat.getColor(context, R.color.purple_700)
    private val paint = Paint().apply {
        color = colorBlue
        style = Paint.Style.FILL_AND_STROKE
    }

    private var invincibleCountDownTimer = 0
    private var moveCountDownTimer = 0
    private var attackCoolDownTimer = 0
    private var direction = DIRECTION_STILL
    private var moveSpeed = .5f

    lateinit var bounds: Rect
    var status = STATUS_ALIVE
    val projectiles = ArrayList<Projectile>()
    val healthBar = HealthBar(context, 18, 18, colorPurple)

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

    private fun attack() {
        attackCoolDownTimer = 30
        projectiles.apply {
            val centerX = (x + x + this@Enemy.size) / 2
            val centerY = (y + y + this@Enemy.size) / 2
            add(Projectile(context, x, centerY, unit, DIRECTION_LEFT, .5f))
            add(Projectile(context, centerX, y, unit, DIRECTION_UP, .5f))
            add(Projectile(context, x + this@Enemy.size, centerY, unit, DIRECTION_RIGHT, .5f))
            add(Projectile(context, centerX, y + this@Enemy.size, unit, DIRECTION_DOWN, .5f))
        }
    }

    fun draw(canvas: Canvas) {
        paint.color = when (status) {
            STATUS_DEAD, STATUS_INVINCIBLE -> if (paint.color == colorBlue) colorTransparent else colorBlue
            else -> colorBlue
        }
        canvas.drawRect(Rect(x, y, x + size, min(y + size, canvas.height)), paint)
    }

    fun update() {
        if (status == STATUS_INVINCIBLE) {
            if (invincibleCountDownTimer > 0) {
                invincibleCountDownTimer--
            } else {
                status = STATUS_ALIVE
            }
        }
        if (moveCountDownTimer > 0) {
            moveCountDownTimer--
        } else {
            setMovement(Random.nextInt(DIRECTION_DOWN..DIRECTION_UP), 6)
        }
        if (attackCoolDownTimer > 0) {
            attackCoolDownTimer--
        }
    }

    fun action() {
        if (moveCountDownTimer > 0) {
            move(direction, moveSpeed)
        }
        if (direction == DIRECTION_STILL && attackCoolDownTimer == 0 && moveCountDownTimer == 3) {
            attack()
        }
    }

    fun knockBack(direction: Int, strength: Float) {
        move(direction, strength)
        setMovement(DIRECTION_STILL, 3)
    }

    fun invincible(time: Int) {
        status = STATUS_INVINCIBLE
        invincibleCountDownTimer = time
    }

    fun getDamaged(damage: Int) {
        healthBar.decrease(damage)
        if (healthBar.hp > 0) {
            invincible(6)
        } else {
            status = STATUS_DEAD
        }
    }
}