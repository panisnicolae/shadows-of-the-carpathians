package io.github.feykro.sotc.weapons.ranged

import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.Weapon
import io.github.feykro.sotc.weapons.projectile.ProjectileManager

abstract class RangedWeapon(
    texture: Texture,
    protected val projectileManager: ProjectileManager
) : Weapon(texture) {

    abstract val projectileSpeed: Float
}
