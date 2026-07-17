package io.github.feykro.sotc.weapons.projectile

import com.badlogic.gdx.graphics.Texture

class Bullet(
    texture: Texture
) : Projectile(texture) {

    override val width = 4f
    override val height = 4f

    init {
        sprite.setOriginCenter()
    }

    override fun update(delta: Float) {
        super.update(delta)
    }
}
