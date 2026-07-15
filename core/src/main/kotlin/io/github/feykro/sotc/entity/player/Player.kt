package io.github.feykro.sotc.entity.player

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
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
    private val hitbox = Polygon(
        floatArrayOf(
            0f, 0f,
            HITBOX_WIDTH, 0f,
            HITBOX_WIDTH, HITBOX_HEIGHT,
            0f, HITBOX_HEIGHT
        )
    )
    private var speed = 200f
    private var health = 100
    private var maxHealth = 100

    private var level = 1
    private var xp = 0
    private var xpToNextLevel = 100

    lateinit var weapon: Weapon
    private var facingRight = true

    fun getHitbox(): Polygon = hitbox
    fun getHealth(): Int = health
    fun getMaxHealth(): Int = maxHealth
    fun getLevel() = level
    fun getXp() = xp
    fun getXpToNextLevel() = xpToNextLevel

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

        hitbox.setPosition(
            x + HITBOX_OFFSET_X,
            y + HITBOX_OFFSET_Y
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

    fun addXp(amount: Int): Boolean {
        xp += amount

        var leveledUp = false

        while (xp >= xpToNextLevel) {
            xp -= xpToNextLevel
            level++
            xpToNextLevel = (xpToNextLevel * 1.25f).toInt()
            leveledUp = true
        }

        return leveledUp
    }

    fun increaseMaxHealth(amount: Int) {
        maxHealth += amount
        health = maxHealth
    }

    fun increaseSpeed(multiplier: Float) {
        speed *= multiplier
    }

    private fun isBlocked(nextX: Float, nextY: Float, objects: MapObjects?): Boolean {
        if (objects == null) return false

        val testPolygon = Polygon(
            floatArrayOf(
                0f, 0f,
                HITBOX_WIDTH, 0f,
                HITBOX_WIDTH, HITBOX_HEIGHT,
                0f, HITBOX_HEIGHT
            )
        )

        testPolygon.setPosition(
            nextX + HITBOX_OFFSET_X,
            nextY + HITBOX_OFFSET_Y
        )

        for (objectMap in objects) {
            if (objectMap is PolygonMapObject) {

                if (
                    Intersector.overlapConvexPolygons(
                        testPolygon,
                        objectMap.polygon
                    )
                ) {
                    return true
                }
            }
        }

        return false
    }

}
