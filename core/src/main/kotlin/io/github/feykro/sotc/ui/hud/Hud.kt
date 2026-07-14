package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.feykro.sotc.input.FloatingJoystick
import io.github.feykro.sotc.upgrade.Upgrade

class Hud {

    val stage = Stage(ScreenViewport())
    private var skin: Skin? = null

    private val table = Table()
    private val xpTable = Table()
    private lateinit var levelLabel: Label

    private val font: BitmapFont

    private val fpsCounter: FpsCounter

    private val healthBar = FrameBar("ui/RED HEALTHBAR/PNG")
    private val xpBar = FrameBar("ui/GREEN HEALTHBAR/PNG")
    private lateinit var killsLabel: Label
    private lateinit var healthLabel: Label

    lateinit var joystick: FloatingJoystick
    lateinit var attackButton: ImageButton

    fun getFont(): BitmapFont = font

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

        levelLabel = Label("Lv. 1", Label.LabelStyle(font, Color.WHITE))
        levelLabel.setAlignment(Align.center)

        killsLabel = Label("Kills: 0", Label.LabelStyle(font, Color.WHITE))
        healthLabel = Label(
            "100 / 100",
            Label.LabelStyle(font, Color.WHITE)
        )
        healthLabel.setAlignment(Align.center)

        table.setFillParent(true)
        table.top()
        table.pad(10f)

        xpTable.setFillParent(true)
        xpTable.bottom()
        xpTable.padBottom(20f)
        xpBar.setSize(480f, 160f)
        xpTable.defaults().center()
        xpTable.add(levelLabel)
            .padRight(10f)
            .center()

        xpTable.add(xpBar)


        table.add(fpsCounter.root).left()
        layoutHud()

        stage.addActor(healthBar)
        stage.addActor(healthLabel)
        stage.addActor(killsLabel)
        stage.addActor(xpTable)
        stage.addActor(table)
    }

    fun update(delta: Float) {
        fpsCounter.update()
        stage.act(delta)
    }

    fun render(batch: Batch) {

        stage.draw()

        if (::joystick.isInitialized) {
            batch.projectionMatrix = stage.camera.combined

            batch.begin()
            joystick.render(batch)
            batch.end()
        }
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        layoutHud()
    }

    fun dispose() {
        font.dispose()
        stage.dispose()
    }

    fun setHealth(current: Int, max: Int) {
        healthBar.setValue(current, max)
    }

    fun setXp(current: Int, max: Int) {
        xpBar.setValue(current, max)
    }

    fun setLevel(level: Int) {
        levelLabel.setText("Lv. $level")
    }

    fun setKills(kills: Int) {
        killsLabel.setText("Kills: $kills")
    }

    private fun layoutHud() {
        healthBar.setSize(320f,160f)
        healthBar.setPosition(
            20f,
            stage.viewport.worldHeight - 180f
        )
        killsLabel.setPosition(healthBar.x+25f, healthBar.y+25f)
        healthLabel.setPosition(
            healthBar.x + healthBar.width + healthLabel.width / 2f,
            healthBar.y + healthBar.height / 2f + healthLabel.height / 2f,
            Align.center
        )
    }
    fun createMobileControls(skin: Skin) {

        joystick = FloatingJoystick(
            skin,
            120f
        )
        attackButton = ImageButton(skin)

        attackButton.setBounds(
            stage.viewport.worldWidth - 400f,
            160f,
            250f,
            250f
        )
        stage.addActor(attackButton)
        stage.addListener(object : com.badlogic.gdx.scenes.scene2d.InputListener() {

            override fun touchDown(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {

                // doar in coltul stanga jos
                if (x < stage.viewport.worldWidth / 3f &&
                    y < stage.viewport.worldHeight * 0.5f) {
                    joystick.touchDown(x, y)
                    return true
                }

                return false
            }

            override fun touchDragged(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                joystick.touchDragged(x, y)
            }

            override fun touchUp(
                event: InputEvent,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ) {
                joystick.touchUp()
            }
        })
    }
}
