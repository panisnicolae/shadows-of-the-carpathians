package io.github.feykro.sotc.weapons.melee

import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.WeaponStats

class Axe(texture: Texture) : MeleeWeapon(texture) {
    override val stats = WeaponStats(
        baseDamage = 18,
        damagePerLevel = 3,

        baseAttackSpeed = 0.8f,
        attackSpeedPerLevel = 0.03f
    )
    override val weaponLength = 31f
}
