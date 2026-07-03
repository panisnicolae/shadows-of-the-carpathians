package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table

class FpsCounter(font: BitmapFont) {

    val root = Table()

    private val label = Label(
        "",
        Label.LabelStyle(font, Color.WHITE)
    )

    init {
        root.add(label)
    }

    fun update() {
        val runtime = Runtime.getRuntime()

        val usedMemory =
            (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024

        label.setText(
            """
            FPS: ${Gdx.graphics.framesPerSecond}
            RAM: ${usedMemory} MB
            """.trimIndent()
        )
    }
}
