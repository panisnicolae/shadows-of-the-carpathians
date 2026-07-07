package io.github.feykro.sotc.weapons.projectile

import com.badlogic.gdx.utils.Pool

class ProjectilePool<T : Projectile>(
    private val creator: () -> T
) : Pool<T>() {

    override fun newObject(): T = creator()
}
