package io.github.feykro.sotc.entity.player

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.feykro.sotc.weapons.Weapon

class Player(private val texture: Texture) {
    var x = 10f
    var y = 10f

    companion object {
        const val WIDTH = 32f
        const val HEIGHT = 32f

        const val HITBOX_WIDTH = 18f
        const val HITBOX_HEIGHT = 32f

        const val HITBOX_OFFSET_X = 7f
        const val HITBOX_OFFSET_Y = 0f
    }
    private val speed = 200f
    private var health = 100
    lateinit var weapon: Weapon
    private val hitbox = Rectangle()
    private var facingRight = true

    fun getHitbox(): Rectangle = hitbox

    fun lookAt(targetX: Float) {
        facingRight = targetX > x + WIDTH / 2f
    }

    fun update(delta: Float, direction: Vector2, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {
        // În jocuri cu țintire, nu mai schimbăm facingRight aici,
        // ci în lookAt() pe care o apelăm din GameScreen

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

        hitbox.set(
            x + HITBOX_OFFSET_X,
            y + HITBOX_OFFSET_Y,
            HITBOX_WIDTH,
            HITBOX_HEIGHT
        )
        weapon.setOwnerPosition(x + WIDTH / 2f, y + HEIGHT / 2f)
        weapon.update(delta)
    }

    fun render(batch: Batch) {
        val drawX = if (facingRight) x else x + WIDTH
        val drawWidth = if (facingRight) WIDTH else -WIDTH

        batch.draw(
            texture,
            drawX,
            y,
            drawWidth,
            HEIGHT
        )

        // Ajustăm poziția armei în funcție de direcția jucătorului

        weapon.render(
            batch,
            x + WIDTH / 2f,
            y + HEIGHT / 2f
        )
    }

    fun attack() {
        weapon.attack()
    }

    fun takeDamage(amount: Int) {
        health -= amount
    }

    fun isAlive(): Boolean = health > 0

    private fun isBlocked(nextX: Float, nextY: Float, objects: MapObjects?): Boolean {
        if (objects == null) return false

        val testHitbox = Rectangle(
            nextX + HITBOX_OFFSET_X,
            nextY + HITBOX_OFFSET_Y,
            HITBOX_WIDTH,
            HITBOX_HEIGHT
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
