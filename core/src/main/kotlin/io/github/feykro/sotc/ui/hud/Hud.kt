package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ScreenViewport

class Hud {

    private val stage = Stage(ScreenViewport())

    private val table = Table()

    private val font: BitmapFont

    private val fpsCounter: FpsCounter

    init {

        val generator =
            FreeTypeFontGenerator(Gdx.files.internal("ui/Roboto-Regular.ttf"))

        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 24
            color = Color.WHITE
        }

        font = generator.generateFont(parameter)
        generator.dispose()

        fpsCounter = FpsCounter(font)

        table.setFillParent(true)
        table.top()
        table.pad(10f)

        table.add(fpsCounter.root).left()

        stage.addActor(table)
    }

    fun update(delta: Float) {
        fpsCounter.update()
        stage.act(delta)
    }

    fun render() {
        stage.draw()
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    fun dispose() {
        font.dispose()
        stage.dispose()
    }
}
