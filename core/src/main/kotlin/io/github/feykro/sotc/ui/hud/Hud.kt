package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
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

    lateinit var movePad: Touchpad
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
        stage.addActor(xpTable)
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

    private fun layoutHud() {
        healthBar.setSize(320f,160f)
        healthBar.setPosition(
            20f,
            stage.viewport.worldHeight - 180f
        )
    }

    fun showUpgradeMenu(upgrades: List<Upgrade>, onSelected: (Upgrade) -> Unit) {
        val dialog = Table(skin)
        dialog.setFillParent(true)
        dialog.center()
        // Adaugă butoane pentru fiecare upgrade din listă
        upgrades.forEach { upgrade ->
            val button = TextButton("${upgrade.title}\n${upgrade.description}", skin)
            button.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    onSelected(upgrade)
                    dialog.remove()
                }
            })
            dialog.add(button).pad(10f).row()
        }
        stage.addActor(dialog)
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
