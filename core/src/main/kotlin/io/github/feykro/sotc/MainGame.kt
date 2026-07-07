package io.github.feykro.sotc

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.feykro.sotc.input.DesktopInputManager
import io.github.feykro.sotc.input.InputManager
import io.github.feykro.sotc.screens.MainMenuScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class MainGame : Game() {

    val batch: Batch by lazy { SpriteBatch() }
    val shapeRenderer: ShapeRenderer by lazy { ShapeRenderer() }
    val assetManager = AssetManager()
    val inputManager: InputManager = DesktopInputManager()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        setScreen(MainMenuScreen(this))
        assetManager.load("player.png", Texture::class.java)
        assetManager.load("enemies/Skeleton enemy.png", Texture::class.java)
        //assetManager.load("enemies/varcolac.png", Texture::class.java)

        assetManager.load("weapons/WR_NightBoundSet _Sword_009.png", Texture::class.java)
        assetManager.load("weapons/WR_NightBoundSet _Axe_002.png", Texture::class.java)
        assetManager.load("weapons/WR_NightBoundSet _Gun_001.png", Texture::class.java)
        assetManager.load("weapons/WR_NightBoundSet _Gun_005.png", Texture::class.java)
        assetManager.load("weapons/WR_NightBoundSet _Gun_010.png", Texture::class.java)
        assetManager.load("weapons/bullet.png", Texture::class.java)

        assetManager.finishLoading()
    }

    override fun dispose() {
        batch.dispose()
        assetManager.dispose()
        shapeRenderer.dispose()
    }
}
