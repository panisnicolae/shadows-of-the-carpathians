package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.math.Vector2
import io.github.feykro.sotc.entity.player.Player
import io.github.feykro.sotc.weapons.projectile.ProjectileManager

enum class NecromancerAnimationState {
    IDLE,
    WALK,
    HURT,
    ATTACK,
    DEATH
}

class Necromancer(
    x: Float,
    y: Float,
    private val idleTexture: Texture,
    private val walkTexture: Texture,
    private val attackTexture: Texture,
    private val deathTexture: Texture,
    private val hurtTexture: Texture,
    private val projectileManager: ProjectileManager
) : Enemy(x, y, idleTexture) {

    override val speed = 60f
    override val xpReward = 50

    override val WIDTH = 96f
    override val HEIGHT = 96f

    override val HITBOX_WIDTH = 20f
    override val HITBOX_HEIGHT = 36f
    override val HITBOX_OFFSET_X = 38f
    override val HITBOX_OFFSET_Y = 32f

    override val attackRange = 250f

    private var stateTime = 0f
    private var damageDone = false
    private lateinit var player: Player

    private var animationState = NecromancerAnimationState.IDLE

    private val idleAnimation: Animation<TextureRegion>
    private val walkAnimation: Animation<TextureRegion>
    private val attackAnimation: Animation<TextureRegion>
    private val deathAnimation: Animation<TextureRegion>
    private val hurtAnimation: Animation<TextureRegion>

    init {

        val idleRegions = TextureRegion.split(idleTexture, 96, 96)
        val walkRegions = TextureRegion.split(walkTexture, 96, 96)
        val attackRegions = TextureRegion.split(attackTexture, 128, 128)
        val deathRegions = TextureRegion.split(deathTexture, 96,96)
        val hurtRegions = TextureRegion.split(hurtTexture, 96, 96)


        idleAnimation = Animation(0.15f, *idleRegions[0])
        walkAnimation = Animation(0.10f, *walkRegions[0])
        attackAnimation = Animation(0.10f, *attackRegions[0])
        deathAnimation = Animation(0.05f, *deathRegions[0])
        hurtAnimation = Animation(0.10f, *hurtRegions[0])

        idleAnimation.playMode = Animation.PlayMode.LOOP
        walkAnimation.playMode = Animation.PlayMode.LOOP
        attackAnimation.playMode = Animation.PlayMode.NORMAL
        deathAnimation.playMode = Animation.PlayMode.NORMAL
        hurtAnimation.playMode = Animation.PlayMode.NORMAL
    }

    override fun update(delta: Float, player: Player, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {
        this.player = player
        stateTime += delta

        if (animationState == NecromancerAnimationState.ATTACK) {
            if (!damageDone && stateTime >= 0.8f) {
                shoot()
                damageDone = true
            }

            if (attackAnimation.isAnimationFinished(stateTime)) {
                animationState = NecromancerAnimationState.IDLE
                stateTime = 0f
                damageDone = false
            }
            return
        }

        if (animationState == NecromancerAnimationState.DEATH) {
            return
        }

        super.update(delta, player, worldWidth, worldHeight, collisionObjects)

        if (
            animationState != NecromancerAnimationState.HURT &&
            animationState != NecromancerAnimationState.ATTACK
        ) {
            animationState =
                if (direction.isZero)
                    NecromancerAnimationState.IDLE
                else
                    NecromancerAnimationState.WALK
        }

        if (animationState == NecromancerAnimationState.HURT &&
            hurtAnimation.isAnimationFinished(stateTime)
        ) {

            animationState =
                if (direction.isZero)
                    NecromancerAnimationState.IDLE
                else
                    NecromancerAnimationState.WALK

            stateTime = 0f
        }
    }

    override fun render(batch: Batch) {
        val frame = when (animationState) {
            NecromancerAnimationState.IDLE -> idleAnimation.getKeyFrame(stateTime)
            NecromancerAnimationState.WALK -> walkAnimation.getKeyFrame(stateTime)
            NecromancerAnimationState.HURT -> hurtAnimation.getKeyFrame(stateTime)
            NecromancerAnimationState.ATTACK -> attackAnimation.getKeyFrame(stateTime)
            NecromancerAnimationState.DEATH -> deathAnimation.getKeyFrame(stateTime)
        }

        if (animationState == NecromancerAnimationState.ATTACK) {
            val drawX = if (facingRight) x - 16f else x + WIDTH + 16f
            batch.draw(frame, drawX, y - 16f, if (facingRight) 128f else -128f, 128f)
        } else {
            val drawX = if (facingRight) x else x + WIDTH
            batch.draw(frame, drawX, y, if (facingRight) WIDTH else -WIDTH, HEIGHT)
        }
    }

    private fun shoot() {
        val drawX = if (facingRight) x - 16f else x + WIDTH + 16f
        val drawY = y - 16f
        val staffOffsetX = 90f
        val staffOffsetY = 90f

        val spawnX = if (facingRight)
            drawX + staffOffsetX
        else
            drawX - staffOffsetX
        val spawnY = drawY + staffOffsetY

        val direction = Vector2(player.x + Player.WIDTH / 2f - spawnX, player.y + Player.HEIGHT / 2f - spawnY).nor()

        projectileManager.spawnMagic(
            spawnX,
            spawnY,
            direction,
            220f,
            20
        )
    }

    override fun takeDamage(amount: Int) {
        if (animationState == NecromancerAnimationState.DEATH)
            return

        super.takeDamage(amount)

        animationState =
            if (isAlive())
                NecromancerAnimationState.HURT
            else
                NecromancerAnimationState.DEATH

        stateTime = 0f
    }

    override fun attack() {
        if (animationState == NecromancerAnimationState.DEATH)
            return

        animationState = NecromancerAnimationState.ATTACK
        stateTime = 0f
        damageDone = false
    }

    override fun canBeRemoved(): Boolean {
        return animationState == NecromancerAnimationState.DEATH &&
            deathAnimation.isAnimationFinished(stateTime)
    }

    override fun canBeHit(): Boolean {
        return animationState != NecromancerAnimationState.DEATH
    }
}
