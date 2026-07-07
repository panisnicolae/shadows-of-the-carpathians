package io.github.feykro.sotc.weapons.melee

import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.WeaponStats

class Sword(texture: Texture) : MeleeWeapon(texture) {

    override val stats = WeaponStats(
        baseDamage = 15,
        damagePerLevel = 2,

        baseAttackSpeed = 3f,
        attackSpeedPerLevel = 0.2f
    )
    override val weaponLength = 31f
}
