package io.github.feykro.sotc.entity

import com.badlogic.gdx.graphics.Texture

enum class RitualState {
    LOCKED,
    READY,
    COMPLETED
}

class RitualStone(
    val x: Float,
    val y: Float,
    val texture: Texture
) {

}
