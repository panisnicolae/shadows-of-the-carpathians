package io.github.feykro.sotc.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.ScreenUtils
import io.github.feykro.sotc.MainGame

class GameOverScreen(game: MainGame) : BaseScreen(game) {

    private val font = BitmapFont()

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)

        camera.update()
        game.batch.projectionMatrix = camera.combined

        game.batch.begin()
        font.draw(game.batch, "GAME OVER", 120f, 110f)
        font.draw(game.batch, "Press ENTER to restart", 80f, 90f)
        game.batch.end()

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.screen = GameScreen(game)
        }
    }

    override fun dispose() {
        font.dispose()
    }
}
