package io.github.feykro.sotc.weapons.ranged

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import io.github.feykro.sotc.weapons.WeaponStats
import io.github.feykro.sotc.weapons.projectile.ProjectileManager

class Blunderbuss(
    texture: Texture,
    projectileManager: ProjectileManager
) : RangedWeapon(texture, projectileManager) {
    override val stats = WeaponStats(
        baseDamage = 12,
        damagePerLevel = 2,
        baseAttackSpeed = 1f,
        attackSpeedPerLevel = 0.05f
    )

    override val projectileSpeed = 400f
    override val weaponLength = 29f

    override fun fire() {
        val topPos = getTopPosition()

        repeat(6) {
            val dir = getDirection()
            dir.rotateDeg(MathUtils.random(-12f, 12f))

            projectileManager.spawnBullet(
                x = topPos.x,
                y = topPos.y,
                direction = dir,
                speed = projectileSpeed,
                damage = damage
            )
        }
    }
}
