package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class FrameBar(
    folder: String
) : Actor() {

    private val frames = mutableListOf<Texture>()

    private var value = 0
    private var maxValue = 100

    init {
        for (i in 1..56) {
            frames += Texture(
                Gdx.files.internal("$folder/$i.png")
            )
        }

        setSize(160f, 160f)
    }

    fun setValue(value: Int, maxValue: Int) {
        this.value = value
        this.maxValue = maxValue
    }

    override fun draw(batch: Batch, parentAlpha: Float) {

        val percent =
            if (maxValue == 0) 0f
            else value.toFloat() / maxValue

        val index = (percent * (frames.size - 1))
            .toInt()
            .coerceIn(0, frames.size - 1)

        batch.draw(
            frames[index],
            x,
            y,
            width,
            height
        )
    }

    fun dispose() {
        frames.forEach { it.dispose() }
    }
}
