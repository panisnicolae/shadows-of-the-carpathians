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
    lateinit var weapon: Weapon
    private val hitbox = Rectangle()
    fun getHitbox(): Rectangle = hitbox

    fun update(delta: Float, direction: Vector2, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {
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
    }

    fun render(batch: Batch) {
        batch.draw(
            texture,
            x,
            y,
            WIDTH,
            HEIGHT
        )

        weapon.render(
            batch,
            x,
            y
        )
    }

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
