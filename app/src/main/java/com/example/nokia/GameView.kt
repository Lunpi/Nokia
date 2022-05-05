package com.example.nokia

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import com.example.nokia.GameUtils.Companion.STATUS_ALIVE
import com.example.nokia.GameUtils.Companion.STATUS_DEAD
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.ceil
import kotlin.random.Random

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val unit = resources.getDimension(R.dimen.unit_size).toInt()
    private val dp = resources.displayMetrics.density
    private val bg = ContextCompat.getDrawable(context, R.drawable.bg_woods)
    private val shakeAnimator = ValueAnimator.ofFloat(0f, Random.nextInt(-4, 4) * dp).apply {
        interpolator = DecelerateInterpolator()
        duration = 20
        repeatCount = 5
        addListener(
            onRepeat = {
                (it as ValueAnimator).setFloatValues(0f, Random.nextInt(-4, 4) * dp)
                translationX = it.animatedValue as Float
                translationY = it.animatedValue as Float
            },
            onEnd = {
                translationX = 0f
                translationY = 0f
            }
        )
    }

    private lateinit var bounds: Rect
    private lateinit var apple: Apple
    private lateinit var enemy: Enemy

    val snake = Snake(context, unit * 3, unit * 3, unit)

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
        enemy = Enemy(
            context,
            w - (w % unit) - unit * 5,
            h - (h % unit) - unit * 5,
            unit * 2
        )
        bg?.setBounds(
            left - ceil(dp * 4).toInt(),
            top - ceil(dp * 4).toInt(),
            right + ceil(dp * 4).toInt(),
            bottom + ceil(dp * 4).toInt()
        )
        bounds = Rect(0, 0, w, h).also {
            snake.bounds = it
            enemy.bounds = it
            apple.bounds = it
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bg?.draw(canvas)

        snake.run {
            update()
            move()
        }
        enemy.run {
            update()
            action()
            projectiles.forEach {
                it.move()
            }
        }

        // check collision
        val collision = collideWith()
        when (collision) {
            is Snake -> {
                snake.status = STATUS_DEAD
            }
            is Enemy -> {
                if (enemy.status == STATUS_ALIVE) {
                    shakeAnimator.start()
                    enemy.knockBack(snake.direction, 3f)
                    enemy.invincible(6)
                }
            }
            is Apple -> {
                snake.grow()
                apple.relocate()
            }
            is Projectile -> {
                if (snake.status == STATUS_ALIVE) {
                    shakeAnimator.start()
                    snake.invincible(6)
                }
            }
        }

        snake.draw(canvas)
        enemy.draw(canvas)
        if (collision !is Apple) {
            apple.draw(canvas)
        }
        enemy.projectiles.run {
            forEach { projectile ->
                if (collision != projectile) {
                    projectile.draw(canvas)
                }
            }
            filter {
                (it.x !in bounds.left..bounds.right) || (it.y !in bounds.top..bounds.bottom) || it == collision
            }.forEach {
                remove(it)
            }
        }
    }

    private fun collideWith(): Any? {
        val snakeHeadHitBox = Rect(snake.x, snake.y, snake.x + snake.size, snake.y + snake.size)
        val snakeBodyHitBox = ArrayList<Rect>().apply {
            snake.body.forEach {
                add(Rect(it.x, it.y, it.x + snake.size, it.y + snake.size))
            }
        }
        val enemyHitBox = Rect(enemy.x, enemy.y, enemy.x + enemy.size, enemy.y + enemy.size)
        val appleHitBox = Rect(apple.x, apple.y, apple.x + apple.size, apple.y + apple.size)

        if (snake.body.subList(0, snake.body.size - 1).contains(snake.body.last())) {
            return snake
        }
        if (snakeHeadHitBox.intersect(enemyHitBox)) {
            return enemy
        }
        enemy.projectiles.forEach { projectile ->
            val projectileHitBox = Rect(
                projectile.x - projectile.size,
                projectile.y - projectile.size,
                projectile.x + projectile.size,
                projectile.y + projectile.size
            )
            if (snakeHeadHitBox.intersect(projectileHitBox)) {
                return projectile
            }
            snakeBodyHitBox.forEach { bodyHitBox ->
                if (bodyHitBox.intersect(projectileHitBox)) {
                    return projectile
                }
            }
        }
        if (snakeHeadHitBox.intersect(appleHitBox)) {
            return apple
        }
        return null
    }
}