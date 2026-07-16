package io.github.feykro.sotc.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.feykro.sotc.Ritual
import io.github.feykro.sotc.entity.player.Player

class MiniMap(
    texture: Texture,
    private val worldWidth: Float,
    private val worldHeight: Float
) {

    private val sprite = Sprite(texture)

    var x = 0f
    var y = 0f

    var width = 320f
    var height = 480f

    fun renderBackground(batch: Batch) {

        sprite.setBounds(
            x,
            y,
            width,
            height
        )

        sprite.draw(batch)
    }

    fun renderMarkers(
        shapeRenderer: ShapeRenderer,
        player: Player,
        rituals: List<Ritual>
    ) {

        // Monumente
        for (ritual in rituals) {

            val rx = x + ritual.x / worldWidth * width
            val ry = y + ritual.y / worldHeight * height

            shapeRenderer.rect(
                rx - 2f,
                ry - 2f,
                4f,
                4f
            )
        }

        // Jucător
        val px = x + (player.x + Player.WIDTH / 2f) / worldWidth * width
        val py = y + (player.y + Player.HEIGHT / 2f) / worldHeight * height

        shapeRenderer.circle(
            px,
            py,
            3f
        )
    }
}
