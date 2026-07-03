package io.github.feykro.sotc.weapons

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils

enum class WeaponType {
    SWORD,
    AXE
}

abstract class Weapon(
    texture: Texture,
    val damage: Int,
    val attackSpeed: Float
) {

    protected val sprite = Sprite(texture)

    protected var rotation = 0f

    init {
        sprite.setOrigin(8f, 2f)
    }

    fun lookAt(
        ownerX: Float,
        ownerY: Float,
        targetX: Float,
        targetY: Float
    ) {
        rotation = MathUtils.atan2(
            targetY - ownerY,
            targetX - ownerX
        ) * MathUtils.radiansToDegrees-90f
    }

    abstract fun update(delta: Float)

    open fun render(
        batch: Batch,
        x: Float,
        y: Float
    ) {
        sprite.setPosition(x+10f, y+8f)
        sprite.rotation = rotation
        sprite.draw(batch)
    }
}
