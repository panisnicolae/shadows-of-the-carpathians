package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

enum class EnemyState {
    WANDER,
    CHASE
}

abstract class Enemy(
    var x: Float,
    var y: Float,
    protected val texture: Texture
) {
    companion object {
        const val WIDTH = 32f
        const val HEIGHT = 32f
    }

    protected open val speed = 50f

    protected val direction = Vector2()
    protected var wanderTimer = 0f

    private var state = EnemyState.WANDER
    private val detectionRadius = 150f

    open fun update(delta: Float, playerX: Float, playerY: Float, worldWidth: Float, worldHeight: Float) {
        val distance = Vector2.dst(x, y, playerX, playerY)
        state =
            if (distance < detectionRadius)
                EnemyState.CHASE
            else
                EnemyState.WANDER
        when (state) {
            EnemyState.WANDER -> {
                wanderTimer -= delta

                if (wanderTimer <= 0f) {
                    direction.set(
                        MathUtils.random(-1f, 1f),
                        MathUtils.random(-1f, 1f)
                    ).nor()

                    wanderTimer = MathUtils.random(1f, 3f)
                }

                x += direction.x * speed * delta
                y += direction.y * speed * delta

                x = MathUtils.clamp(x, 0f, worldWidth - WIDTH)
                y = MathUtils.clamp(y, 0f, worldHeight - HEIGHT)
            }
            EnemyState.CHASE -> {
                direction.set(
                    playerX - x,
                    playerY - y
                ).nor()

                x += direction.x * speed * delta
                y += direction.y * speed * delta
            }
        }
    }

    open fun render(batch: Batch) {
        batch.draw(texture, x, y, WIDTH, HEIGHT)
    }
}
