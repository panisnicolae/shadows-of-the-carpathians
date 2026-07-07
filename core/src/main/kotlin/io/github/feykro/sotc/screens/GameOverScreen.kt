package io.github.feykro.sotc.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.feykro.sotc.MainGame

class GameOverScreen(game: MainGame) : BaseScreen(game) {

    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("metal/metal-ui.json"))
    private val table = Table()

    private lateinit var font: BitmapFont
    private lateinit var title: Label

    override fun show() {
        Gdx.input.inputProcessor = stage

        val generator = FreeTypeFontGenerator(Gdx.files.internal("LaughTalesDemo.otf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 48
            color = Color.WHITE
        }

        font = generator.generateFont(parameter)
        generator.dispose()

        title = Label("GAME OVER", Label.LabelStyle(font, Color.RED))

        val menuButton = TextButton("Main Menu", skin)
        menuButton.label.setFontScale(2f)

        menuButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.setScreen(MainMenuScreen(game))
            }
        })

        table.setFillParent(true)
        table.defaults().pad(20f)

        table.add(title)
        table.row()
        table.add(menuButton)
            .width(250f)
            .height(80f)

        stage.addActor(table)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
        font.dispose()
    }
}
