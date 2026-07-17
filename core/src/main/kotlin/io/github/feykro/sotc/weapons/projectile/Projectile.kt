package io.github.feykro.sotc.weapons.projectile

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

enum class ProjectileOwner {
    PLAYER,
    ENEMY
}
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

    protected open val width = 32f
    protected open val height = 32f

    var damage = 0
        protected set
    var owner = ProjectileOwner.PLAYER
    protected var active = false
    private val hitbox = Polygon()

    init {
        hitbox.vertices = floatArrayOf(-4f,-4f,4f,-4f,4f,4f,-4f,4f)
    }

    fun getHitbox(): Polygon {
        hitbox.setPosition(x, y)
        hitbox.rotation = sprite.rotation
        return hitbox
    }

    fun spawn(x: Float, y: Float, direction: Vector2, speed: Float, damage: Int) {
        this.x = x
        this.y = y
        this.direction.set(direction).nor()
        this.speed = speed
        this.damage = damage

        this.sprite.rotation = this.direction.angleDeg() - 90f

        val hw = width * 0.4f
        val hh = height * 0.4f
        hitbox.vertices = floatArrayOf(
            -hw, -hh,
            hw, -hh,
            hw, hh,
            -hw, hh
        )

        active = true
    }

    open fun update(delta: Float) {
        if (!active) return

        x += direction.x * speed * delta
        y += direction.y * speed * delta
    }

    open fun render(batch: Batch) {
        if (!active) return

        sprite.setSize(width,height)
        sprite.setOriginCenter()
        sprite.setPosition(x - width / 2f, y - height / 2f)
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
