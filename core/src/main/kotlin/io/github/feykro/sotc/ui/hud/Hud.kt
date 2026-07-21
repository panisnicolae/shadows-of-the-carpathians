package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.github.feykro.sotc.Ritual
import io.github.feykro.sotc.entity.player.Player
import io.github.feykro.sotc.input.FloatingJoystick
import io.github.feykro.sotc.ui.MiniMap
import ktx.actors.plusAssign
import ktx.graphics.use
import ktx.scene2d.*

class Hud {
    val stage = Stage(ScreenViewport())
    private val font: BitmapFont
    private val fpsCounter: FpsCounter
    private val healthBar = FrameBar("ui/RED HEALTHBAR/PNG")
    private val xpBar = FrameBar("ui/GREEN HEALTHBAR/PNG")

    private val levelLabel: Label
    private val killsLabel: Label
    private val healthLabel: Label

    lateinit var joystick: FloatingJoystick
    lateinit var attackButton: ImageButton
    private lateinit var miniMap: MiniMap

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("ui/Roboto-Regular.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 24
            color = Color.WHITE
        }
        font = generator.generateFont(parameter)
        generator.dispose()

        fpsCounter = FpsCounter(font)
        val labelStyle = Label.LabelStyle(font, Color.WHITE)

        levelLabel = Label("Lv. 1", labelStyle)
        killsLabel = Label("Kills: 0", labelStyle)
        healthLabel = Label("100 / 100", labelStyle).apply { setAlignment(Align.center) }

        stage.actors {
            //top left
            table {
                setFillParent(true)
                top().left().pad(20f)

                stack {
                    add(healthBar)
                    container(actor = killsLabel) {
                        left()
                        bottom()
                        padLeft(25f)
                        padBottom(30f)
                    }
                    container(actor = healthLabel) {
                        right()
                        bottom()
                        padRight(20f)
                        padBottom(30f)
                    }
                }.cell(width = 320f, height = 160f)

                row()

                add(fpsCounter.root).left().padTop(10f)
            }

            //top right
            table {
                setFillParent(true)
                top().right().pad(20f)

                container {
                    name = "miniMapContainer"
                }.size(200f)
            }

            //bottom center
            table {
                setFillParent(true)
                bottom().padBottom(20f)

                add(levelLabel)
                    .padBottom(15f)

                add(xpBar)
                    .width(480f)
                    .height(160f)
            }
        }
    }

    fun update(delta: Float) {
        fpsCounter.update()
        stage.act(delta)
    }

    fun render(
        batch: Batch,
        player: Player,
        rituals: List<Ritual>
    ) {

        stage.draw()

        if (::miniMap.isInitialized) {
            miniMap.player = player
            miniMap.rituals = rituals
        }

        if (::joystick.isInitialized) {
            batch.use(stage.camera.combined) {
                joystick.render(it)
            }
        }
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    fun dispose() {
        font.dispose()
        stage.dispose()
    }

    fun setHealth(current: Int, max: Int) {
        healthBar.setValue(current, max)
        healthLabel.setText("$current / $max")
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
        if (::miniMap.isInitialized) {
            miniMap.x = stage.viewport.worldWidth - miniMap.width - 20f
            miniMap.y = stage.viewport.worldHeight - miniMap.height - 20f
        }
    }

    fun createMiniMap(
        texture: Texture,
        worldWidth: Float,
        worldHeight: Float
    ) {
        miniMap = MiniMap(texture, worldWidth, worldHeight)
        val container = stage.root.findActor<com.badlogic.gdx.scenes.scene2d.ui.Container<MiniMap>>("miniMapContainer")
        container?.actor = miniMap
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
