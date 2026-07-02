package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.graphics.Texture

class Strigoi(
    x: Float,
    y: Float,
    texture: Texture
) : Enemy(x, y, texture) {

    override val speed = 70f
}
