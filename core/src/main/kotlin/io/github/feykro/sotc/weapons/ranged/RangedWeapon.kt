package io.github.feykro.sotc.weapons.ranged

import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.Weapon
import io.github.feykro.sotc.weapons.projectile.ProjectileManager

abstract class RangedWeapon(
    texture: Texture,
    protected val projectileManager: ProjectileManager
) : Weapon(texture) {

    abstract val projectileSpeed: Float

    private var attackTimer = 0f

    override val maxRecoil = 6f
    override val recoilRecoverSpeed = 18f

    open override fun update(delta: Float) {
        attackTimer -= delta
        updateRecoil(delta)
    }

    final override fun attack() {
        if (attackTimer > 0f) return

        attackTimer = 1f / attackSpeed

        recoil = maxRecoil

        fire()
    }

    protected abstract fun fire()
}
