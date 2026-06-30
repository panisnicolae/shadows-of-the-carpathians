package io.github.feykro.sotc.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.feykro.sotc.MainGame

abstract class BaseScreen(
    protected val game: MainGame
) : Screen {

    protected val camera = OrthographicCamera()
    protected val viewport = ExtendViewport(320f, 180f, camera)

    override fun show() {
        viewport.apply()
    }

    override fun render(delta: Float) {}

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {}
}
