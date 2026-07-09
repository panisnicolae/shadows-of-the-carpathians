package io.github.feykro.sotc.weapons.ranged

import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.WeaponStats
import io.github.feykro.sotc.weapons.projectile.ProjectileManager

class Carbine(
    texture: Texture,
    projectileManager: ProjectileManager
) : RangedWeapon(texture, projectileManager) {

    override val stats = WeaponStats(
        baseDamage = 20,
        damagePerLevel = 3,
        baseAttackSpeed = 3f,
        attackSpeedPerLevel = 0.1f
    )

    override val projectileSpeed = 500f
    override val weaponLength = 31f

    override fun fire() {
        val topPos = getTopPosition()

        projectileManager.spawnBullet(
            x = topPos.x,
            y = topPos.y,
            direction = getDirection(),
            speed = projectileSpeed,
            damage = damage
        )
    }
}
