package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch

class HealthBar {

    private val frames = mutableListOf<Texture>()

    init {
        for (i in 1 until 57) {
            frames += Texture(
                Gdx.files.internal("ui/RED HEALTHBAR/PNG/$i.png")
            )
        }
    }

    fun render(
        batch: Batch,
        health: Int,
        maxHealth: Int,
        x: Float,
        y: Float
    ) {

        val percent = health.toFloat() / maxHealth

        val index = (percent * (frames.size - 1))
            .toInt()
            .coerceIn(0, frames.size - 1)

        batch.draw(
            frames[index],
            x,
            y,
            160f,
            160f
        )
    }

    fun dispose() {
        frames.forEach { it.dispose() }
    }
}
