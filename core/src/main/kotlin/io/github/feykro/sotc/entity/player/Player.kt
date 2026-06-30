package io.github.feykro.sotc.entity.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class Player {
    var x = 10f
    var y = 10f

    companion object {
        const val WIDTH = 32f
        const val HEIGHT = 32f
    }

    private val speed = 200f

    fun update(delta: Float, direction: Vector2, worldWidth: Float, worldHeight: Float) {
        x += direction.x * speed * delta
        y += direction.y * speed * delta

        x = MathUtils.clamp(x, 0f, worldWidth - WIDTH)
        y = MathUtils.clamp(y, 0f, worldHeight - HEIGHT)
    }
}
