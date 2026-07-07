package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.maps.objects.RectangleMapObject

enum class EnemyState {
    WANDER,
    CHASE
}

abstract class Enemy(
    var x: Float,
    var y: Float,
    protected val texture: Texture
) {
    open val WIDTH = 32f
    open val HEIGHT = 32f

    protected open val speed = 50f
    protected var health = 500

    protected val direction = Vector2()
    protected var wanderTimer = 0f
    protected var facingRight = true
    private val hitbox = Rectangle()

    private var state = EnemyState.WANDER
    private val detectionRadius = 150f

    fun getHitbox(): Rectangle {
        hitbox.set(x+16, y+16, WIDTH/2, HEIGHT/2)
        return hitbox
    }

    open fun takeDamage(amount: Int) {
        if (!isAlive()) return
        health -= amount
        if (health < 0) health = 0
    }

    fun isAlive(): Boolean = health > 0

    open fun update(delta: Float, playerX: Float, playerY: Float, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {
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
            }
            EnemyState.CHASE -> {
                direction.set(
                    playerX - x,
                    playerY - y
                ).nor()
            }
        }

        if (direction.x > 0) facingRight = true
        else if (direction.x < 0) facingRight = false

        val nextX = x + direction.x * speed * delta

        if (!isBlocked(nextX, y, collisionObjects)) {
            x = nextX
        }

        val nextY = y + direction.y * speed * delta

        if (!isBlocked(x, nextY, collisionObjects)) {
            y = nextY
        }

        x = MathUtils.clamp(x, 0f, worldWidth - WIDTH)
        y = MathUtils.clamp(y, 0f, worldHeight - HEIGHT)
    }

    open fun render(batch: Batch) {
        val drawX = if (facingRight) x else x + WIDTH
        val drawWidth = if (facingRight) WIDTH else -WIDTH
        batch.draw(texture, drawX, y, drawWidth, HEIGHT)
    }

    open fun canBeRemoved(): Boolean = !isAlive()

    open fun canBeHit(): Boolean {
        return isAlive()
    }

    private fun isBlocked(
        nextX: Float,
        nextY: Float,
        objects: MapObjects?
    ): Boolean {

        if (objects == null) return false

        val testHitbox = Rectangle(
            nextX + 16f,
            nextY + 16f,
            WIDTH / 2f,
            HEIGHT / 2f
        )

        for (objectMap in objects) {
            if (objectMap is RectangleMapObject) {
                if (testHitbox.overlaps(objectMap.rectangle)) {
                    return true
                }
            }
        }

        return false
    }
}
