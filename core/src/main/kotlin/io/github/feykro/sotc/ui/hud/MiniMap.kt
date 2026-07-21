// În core/src/main/kotlin/.../ui/hud/MiniMap.kt
package io.github.feykro.sotc.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import io.github.feykro.sotc.Ritual
import io.github.feykro.sotc.entity.player.Player
import ktx.graphics.use

class MiniMap(
    texture: Texture,
    private val worldWidth: Float,
    private val worldHeight: Float
) : Widget() {

    private val sprite = Sprite(texture)
    private val shapeRenderer = ShapeRenderer()

    var player: Player? = null
    var rituals: List<Ritual> = listOf()

    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.setBounds(x, y, width, height)
        sprite.draw(batch)

        batch.end()

        shapeRenderer.projectionMatrix = batch.projectionMatrix

        // border
        shapeRenderer.use(ShapeRenderer.ShapeType.Line) { renderer ->
            renderer.color = Color.BLACK
            renderer.rect(x, y, width, height)
        }

        // rituals + player
        shapeRenderer.use(ShapeRenderer.ShapeType.Filled) { renderer ->
            val p = player ?: return@use

            renderer.color = Color.YELLOW
            for (ritual in rituals) {
                val rx = x + ritual.x / worldWidth * width
                val ry = y + ritual.y / worldHeight * height
                renderer.rect(rx - 2f, ry - 2f, 4f, 4f)
            }

            renderer.color = Color.CYAN
            val px = x + (p.x + Player.WIDTH / 2f) / worldWidth * width
            val py = y + (p.y + Player.HEIGHT / 2f) / worldHeight * height
            renderer.circle(px, py, 3f)
        }

        batch.begin()
    }
}
