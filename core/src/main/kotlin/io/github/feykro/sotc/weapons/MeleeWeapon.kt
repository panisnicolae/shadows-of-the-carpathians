package io.github.feykro.sotc.weapons

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch

abstract class MeleeWeapon(
    texture: Texture,
    damage: Int,
    attackSpeed: Float
) : Weapon(texture, damage, attackSpeed) {

    fun attack() {
    }

    override fun update(delta: Float) {
    }
}
