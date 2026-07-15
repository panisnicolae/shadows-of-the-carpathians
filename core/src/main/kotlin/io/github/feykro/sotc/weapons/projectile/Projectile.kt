package io.github.feykro.sotc.weapons.projectile

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

abstract class Projectile(
    texture: Texture
) : Pool.Poolable {

    var x = 0f
        protected set

    var y = 0f
        protected set

    protected val sprite = Sprite(texture)

    protected val direction = Vector2()

    protected var speed = 0f

    var damage = 0
        protected set

    protected var active = false
    private val hitbox = Polygon()

    fun getHitbox(): Polygon {
        hitbox.setVertices(
            floatArrayOf(
                x, y,
                x + sprite.width, y,
                x + sprite.width, y + sprite.height,
                x, y + sprite.height
            )
        )
        return hitbox
    }

    fun spawn(
        x: Float,
        y: Float,
        direction: Vector2,
        speed: Float,
        damage: Int
    ) {
        this.x = x
        this.y = y

        this.direction.set(direction).nor()

        this.speed = speed
        this.damage = damage

        this.sprite.rotation = this.direction.angleDeg() - 90f

        active = true
    }

    open fun update(delta: Float) {
        if (!active) return

        x += direction.x * speed * delta
        y += direction.y * speed * delta

        hitbox.setVertices(
            floatArrayOf(
                x, y,
                x + sprite.width, y,
                x + sprite.width, y + sprite.height,
                x, y + sprite.height
            )
        )
    }

    open fun render(batch: Batch) {
        if (!active) return

        sprite.setPosition(x, y)
        sprite.draw(batch)
    }

    fun isActive(): Boolean = active

    fun destroy() {
        active = false
    }

    override fun reset() {
        x = 0f
        y = 0f

        direction.setZero()

        speed = 0f
        damage = 0

        active = false

        sprite.rotation = 0f
    }
}
