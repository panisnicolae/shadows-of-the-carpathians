package io.github.feykro.sotc.weapons.ranged

import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.WeaponStats
import io.github.feykro.sotc.weapons.projectile.ProjectileManager

class Musket(
    texture: Texture,
    projectileManager: ProjectileManager
) : RangedWeapon(texture, projectileManager) {
    override val stats = WeaponStats(
        baseDamage = 200,
        damagePerLevel = 100,
        baseAttackSpeed = 1.5f,
        attackSpeedPerLevel = 0.03f
    )

    override val projectileSpeed = 700f
    override val weaponLength = 47f

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
