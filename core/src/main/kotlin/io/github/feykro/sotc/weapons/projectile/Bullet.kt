package io.github.feykro.sotc.weapons.projectile

import com.badlogic.gdx.graphics.Texture

class Bullet(
    texture: Texture
) : Projectile(texture) {

    init {
        sprite.setOriginCenter()
    }

    override fun update(delta: Float) {
        super.update(delta)

        // - coliziunea cu inamicii
        // - ieșirea din hartă
    }
}
