package io.github.feykro.sotc.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
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


class MainMenuScreen(
    game: MainGame
): BaseScreen(game) {
    private val skin = Skin(Gdx.files.internal("metal/metal-ui.json"))
    private val stage = Stage(ScreenViewport())
    private val table = Table()
    val textButton = TextButton("Start", skin)
    private lateinit var font: BitmapFont
    private lateinit var titleLabel: Label

    override fun show() {
        Gdx.input.inputProcessor = stage

        val generator = FreeTypeFontGenerator(Gdx.files.internal("LaughTalesDemo.otf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 48
            color = Color.WHITE
        }

        font = generator.generateFont(parameter)
        generator.dispose()

        val labelStyle = Label.LabelStyle(font, Color.WHITE)
        titleLabel = Label("SHADOWS OF THE CARPATHIANS", labelStyle)

        table.setFillParent(true)
        stage.addActor(table)

        table.defaults().pad(20f)

        table.add(titleLabel)
        table.row()

        table.add(textButton)
            .width(250f)
            .height(80f)

        textButton.label.setFontScale(2f)

        textButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //val oldScreen = game.screen
                game.setScreen(GameScreen(game))
                //oldScreen.dispose()
            }
        })
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(1f, 0f, 0f, 1f)
        stage.act()
        stage.draw()

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(GameScreen(game))
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        stage.viewport.update(width, height, true)
    }

    override fun pause() {
        //TODO("Not yet implemented")
    }

    override fun resume() {
        //TODO("Not yet implemented")
    }

    override fun hide() {
        //TODO("Not yet implemented")
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        skin.dispose()
        stage.dispose()
        font.dispose()
    }

}
