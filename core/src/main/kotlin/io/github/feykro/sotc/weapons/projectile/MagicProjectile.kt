package io.github.feykro.sotc.weapons.projectile

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class MagicProjectile(texture: Texture) : Projectile(texture) {

    override val width = 16f
    override val height = 16f
    private val animation: Animation<TextureRegion>
    private var stateTime = 0f

    init {
        val regions = TextureRegion.split(texture, 16, 16)

        animation = Animation(
            0.1f,
            regions[1][10],
            regions[1][11],
            regions[1][12],
        )

        animation.playMode = Animation.PlayMode.LOOP
    }

    override fun update(delta: Float) {
        super.update(delta)
        stateTime += delta
    }

    override fun render(batch: Batch) {
        if (!isActive()) return
        val frame = animation.getKeyFrame(stateTime)

        batch.draw(
            frame,
            x - width / 2f,
            y - height / 2f,
            width / 2f,
            height / 2f,
            width,
            height,
            1f,
            1f,
            sprite.rotation + 90f
        )
    }

    override fun reset() {
        super.reset()
        stateTime = 0f
    }
}
