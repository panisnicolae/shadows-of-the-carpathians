package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.utils.viewport.ScreenViewport

class Hud {

    val stage = Stage(ScreenViewport())

    private val table = Table()

    private val font: BitmapFont

    private val fpsCounter: FpsCounter

    lateinit var movePad: Touchpad
    lateinit var attackButton: ImageButton

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

    fun createMobileControls(skin: Skin) {

        movePad = Touchpad(10f, skin)

        movePad.setBounds(
            100f,
            100f,
            300f,
            300f
        )

        attackButton = ImageButton(skin)

        attackButton.setBounds(
            stage.viewport.worldWidth - 400f,
            160f,
            250f,
            250f
        )

        stage.addActor(movePad)
        stage.addActor(attackButton)
    }
}
