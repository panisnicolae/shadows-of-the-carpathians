package io.github.feykro.sotc.weapons

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import io.github.feykro.sotc.weapons.melee.WeaponState

abstract class Weapon(
    texture: Texture
) {

    protected val sprite = Sprite(texture)

    protected var state = WeaponState.IDLE

    protected var lookRotation = 0f
    protected var rotation = 0f
    protected var ownerX = 0f
    protected var ownerY = 0f
    var level = 1
        private set

    abstract val stats: WeaponStats
    abstract val weaponLength: Float
    private var facingLeft = false

    val damage: Int
        get() = stats.baseDamage + (level - 1) * stats.damagePerLevel

    val attackSpeed: Float
        get() = stats.baseAttackSpeed + (level - 1) * stats.attackSpeedPerLevel

    init {
        sprite.setOrigin(8f, 0f)
    }

    fun upgrade() {
        level++
    }

    fun lookAt(
        ownerX: Float,
        ownerY: Float,
        targetX: Float,
        targetY: Float
    ) {
        facingLeft = targetX < ownerX

        lookRotation = MathUtils.atan2(
            targetY - ownerY,
            targetX - ownerX
        ) * MathUtils.radiansToDegrees - 90f

        if (state == WeaponState.IDLE) {
            rotation = lookRotation
        }
    }

    abstract fun update(delta: Float)

    abstract fun attack()

    open fun render(
        batch: Batch,
        x: Float,
        y: Float
    ) {
        sprite.setFlip(false, facingLeft)

        sprite.setOrigin(
            8f,
            if (facingLeft) sprite.height - 2f else 2f
        )

        sprite.setPosition(
            x - sprite.originX,
            y - sprite.originY
        )

        sprite.rotation =
            if (facingLeft)
                rotation + 180f
            else
                rotation

        sprite.draw(batch)
    }
    protected fun getRenderRotation(): Float {
        return if (facingLeft)
            rotation + 180f
        else
            rotation
    }
    protected fun getDirection(): Vector2 {
        val angle = getRenderRotation() + 90f

        println("Rotation = $rotation")
        println("RenderRotation = ${getRenderRotation()}")
        println("Angle = $angle")
        return Vector2(
            MathUtils.cosDeg(rotation + 90f),
            MathUtils.sinDeg(rotation + 90f)
        )
    }

    fun setOwnerPosition(x: Float, y: Float) {
        ownerX = x
        ownerY = y
    }

    fun getTopPosition(): Vector2 {
        // Distanța de la mâner la vârful țevii
        val distToMuzzle = weaponLength

        // Direcția în care arată arma
        val angle = rotation + 90f

        return Vector2(
            ownerX + MathUtils.cosDeg(angle) * distToMuzzle,
            ownerY + MathUtils.sinDeg(angle) * distToMuzzle
        )
    }
}
