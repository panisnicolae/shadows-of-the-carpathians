package io.github.feykro.sotc

import com.badlogic.gdx.math.Rectangle
import io.github.feykro.sotc.entity.player.Player

class Ritual(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    val bounds = Rectangle(x, y, width, height)

    var completed = false
}
