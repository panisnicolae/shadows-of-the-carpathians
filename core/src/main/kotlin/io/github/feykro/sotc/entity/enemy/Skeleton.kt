package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObjects

enum class SkeletonAnimationState {
    IDLE,
    WALK,
    HURT,
    DEATH
}

class Skeleton(
    x: Float,
    y: Float,
    texture: Texture
) : Enemy(x, y, texture) {
    override val speed = 60f
    private var stateTime = 0f

    override val HEIGHT = 64f
    override val WIDTH = 64f
    private var animationState = SkeletonAnimationState.IDLE
    private val walkAnimation: Animation<TextureRegion>
    private val idleAnimation: Animation<TextureRegion>
    private val hurtAnimation: Animation<TextureRegion>
    private val deathAnimation: Animation<TextureRegion>


    init {
        val frameWidth = 64
        val frameHeight = 64
        val regions = TextureRegion.split(texture, frameWidth, frameHeight)

        idleAnimation = Animation(0.1f, *regions[3].sliceArray(0..3))

        walkAnimation = Animation(0.1f, *regions[2].sliceArray(0..11))
        hurtAnimation = Animation(0.1f, *regions[4].sliceArray(0..2))
        deathAnimation = Animation(0.1f, *regions[1].sliceArray(0..12))

        idleAnimation.playMode = Animation.PlayMode.LOOP
        walkAnimation.playMode = Animation.PlayMode.LOOP
        hurtAnimation.playMode = Animation.PlayMode.NORMAL
        deathAnimation.playMode = Animation.PlayMode.NORMAL
    }

    override fun update(delta: Float, playerX: Float, playerY: Float, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {
        stateTime += delta

        if (animationState == SkeletonAnimationState.DEATH) {
            return
        }
        super.update(delta, playerX, playerY, worldWidth, worldHeight,collisionObjects)
        if (
            animationState != SkeletonAnimationState.HURT &&
            animationState != SkeletonAnimationState.DEATH
        ) {
            animationState =
                if (direction.isZero)
                    SkeletonAnimationState.IDLE
                else
                    SkeletonAnimationState.WALK
        }
        if (animationState == SkeletonAnimationState.HURT &&
            hurtAnimation.isAnimationFinished(stateTime)) {

            animationState =
                if (direction.isZero)
                    SkeletonAnimationState.IDLE
                else
                    SkeletonAnimationState.WALK

            stateTime = 0f
        }
    }

    override fun render(batch: Batch) {
        val currentFrame = when(animationState) {

            SkeletonAnimationState.IDLE ->
                idleAnimation.getKeyFrame(stateTime)

            SkeletonAnimationState.WALK ->
                walkAnimation.getKeyFrame(stateTime)

            SkeletonAnimationState.HURT ->
                hurtAnimation.getKeyFrame(stateTime)

            SkeletonAnimationState.DEATH ->
                deathAnimation.getKeyFrame(stateTime)
        }

        val drawX = if (facingRight) x else x + WIDTH
        val drawWidth = if (facingRight) WIDTH else -WIDTH

        batch.draw(currentFrame, drawX, y, drawWidth, HEIGHT)
    }
    override fun takeDamage(amount: Int) {
        if (animationState == SkeletonAnimationState.DEATH) {
            return
        }

        super.takeDamage(amount)

        if (isAlive()) {
            animationState = SkeletonAnimationState.HURT
        } else {
            animationState = SkeletonAnimationState.DEATH
        }

        stateTime = 0f
    }

    override fun canBeRemoved(): Boolean {
        return animationState == SkeletonAnimationState.DEATH &&
            deathAnimation.isAnimationFinished(stateTime)
    }

    override fun canBeHit(): Boolean {
        return animationState != SkeletonAnimationState.DEATH
    }
}
