package io.github.feykro.sotc.weapons

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch

enum class WeaponState {
    IDLE,
    ATTACKING,
    COOLDOWN
}

abstract class MeleeWeapon(
    texture: Texture,
    damage: Int,
    attackSpeed: Float
) : Weapon(texture, damage, attackSpeed) {

    companion object {
        const val ATTACK_DURATION = 0.20f
        const val COOLDOWN_DURATION = 0.15f
        const val SWING_ANGLE = 120f
    }

    protected var timer = 0f

    override fun attack() {
        if (state != WeaponState.IDLE)
            return

        state = WeaponState.ATTACKING
        timer = 0f
    }

    override fun update(delta: Float) {

        timer += delta

        when (state) {

            WeaponState.IDLE -> {
                rotation = lookRotation
            }

            WeaponState.ATTACKING -> {

                val progress = timer / ATTACK_DURATION

                rotation =
                    lookRotation - SWING_ANGLE / 2f +
                        progress * SWING_ANGLE

                if (timer >= ATTACK_DURATION) {
                    state = WeaponState.COOLDOWN
                    timer = 0f
                }
            }

            WeaponState.COOLDOWN -> {

                rotation = lookRotation

                if (timer >= COOLDOWN_DURATION) {
                    state = WeaponState.IDLE
                    timer = 0f
                }
            }
        }
    }
}
