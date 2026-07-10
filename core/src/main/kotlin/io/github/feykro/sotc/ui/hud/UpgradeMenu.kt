package io.github.feykro.sotc.ui.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import io.github.feykro.sotc.upgrade.Upgrade

class UpgradeMenu(
    private val stage: Stage,
    private val skin: Skin
) {

    private var root: Table? = null

    fun show(
        upgrades: List<Upgrade>,
        onSelected: (Upgrade) -> Unit
    ) {
        Gdx.app.log("MENU", "show")
        hide()

        val overlay = Table()
        overlay.setFillParent(true)
        overlay.setColor(0f, 0f, 0f, 0.75f)

        val window = Table(skin)
        window.background = skin.getDrawable("window-round")

        val title = Label(
            "LEVEL UP!",
            skin
        )
        title.color = Color.GOLD
        title.setAlignment(Align.center)

        val subtitle = Label(
            "Choose one upgrade",
            skin
        )

        subtitle.color = Color.WHITE
        subtitle.setAlignment(Align.center)

        window.defaults()
            .pad(12f)
            .growX()

        window.add(title).center().row()
        window.add(subtitle).center().padBottom(20f).row()

        upgrades.forEach { upgrade ->

            val button = TextButton(
                "${upgrade.title}\n${upgrade.description}",
                skin
            )
            button.label.setFontScale(0.7f)
            button.label.setAlignment(Align.center)

            button.addListener(object : ClickListener() {

                override fun clicked(
                    event: InputEvent?,
                    x: Float,
                    y: Float
                ) {

                    hide()
                    onSelected(upgrade)
                }
            })

            window.add(button)
                .width(700f)
                .height(200f)
                .padBottom(15f)
                .row()
        }

        overlay.add(window)
            .width(1000f)
            .height(800f)
            .center()

        root = overlay

        stage.addActor(overlay)
    }

    fun hide() {
        root?.remove()
        root = null
    }

    fun isVisible(): Boolean {
        return root != null
    }
}
