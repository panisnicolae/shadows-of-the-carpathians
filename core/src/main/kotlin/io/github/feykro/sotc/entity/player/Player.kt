package io.github.feykro.sotc.entity.player

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import io.github.feykro.sotc.weapons.Weapon

enum class PlayerAnimationState {
    IDLE,
    RUN,
    DEATH
}

class Player(private val texture: Texture) {
    var x = 10f
    var y = 10f

    companion object {
        const val WIDTH = 32f
        const val HEIGHT = 32f

        const val HITBOX_WIDTH = 18f
        const val HITBOX_HEIGHT = 27f

        const val HITBOX_OFFSET_X = 7f
        const val HITBOX_OFFSET_Y = 0f
    }
    private val hitbox = Polygon(
        floatArrayOf(
            0f, 0f,
            HITBOX_WIDTH, 0f,
            HITBOX_WIDTH, HITBOX_HEIGHT,
            0f, HITBOX_HEIGHT
        )
    )
    private var speed = 150f
    private var health = 100
    private var maxHealth = 100

    private var level = 1
    private var xp = 0
    private var xpToNextLevel = 100

    lateinit var weapon: Weapon
    private var facingRight = true

    private var hurtTimer = 0f
    private val HURT_DURATION = 0.2f

    private var stateTime = 0f
    private var animationState = PlayerAnimationState.IDLE

    private lateinit var idleAnimation: Animation<TextureRegion>
    private lateinit var runAnimation: Animation<TextureRegion>
    private lateinit var deathAnimation: Animation<TextureRegion>

    init {
        val regions = TextureRegion.split(texture, 32, 32)

        idleAnimation = Animation(0.5f, *regions[0].sliceArray(0..1))
        runAnimation = Animation(0.1f, *regions[3].sliceArray(0..7))
        deathAnimation = Animation(0.2f, *regions[7].sliceArray(0..7))

        idleAnimation.playMode = Animation.PlayMode.LOOP
        runAnimation.playMode = Animation.PlayMode.LOOP
        deathAnimation.playMode = Animation.PlayMode.NORMAL
    }

    fun getHitbox(): Polygon = hitbox
    fun getHealth(): Int = health
    fun getMaxHealth(): Int = maxHealth
    fun getLevel() = level
    fun getXp() = xp
    fun getXpToNextLevel() = xpToNextLevel

    fun lookAt(targetX: Float) {
        facingRight = targetX > x + WIDTH / 2f
    }

    fun update(delta: Float, direction: Vector2, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {

        stateTime += delta

        if (!isAlive()) {

            if (animationState != PlayerAnimationState.DEATH) {
                animationState = PlayerAnimationState.DEATH
                stateTime = 0f
            }
            return

        } else {

            animationState =
                if (direction.isZero)
                    PlayerAnimationState.IDLE
                else
                    PlayerAnimationState.RUN
        }

        if (hurtTimer > 0f) {
            hurtTimer -= delta
        }

        val nextX = x + direction.x * speed * delta

        if (!isBlocked(nextX, y, collisionObjects)) {
            x = nextX
        }

        val nextY = y + direction.y * speed * delta

        if (!isBlocked(x, nextY, collisionObjects)) {
            y = nextY
        }

        x = MathUtils.clamp(x, 0f, worldWidth - WIDTH)
        y = MathUtils.clamp(y, 0f, worldHeight - HEIGHT)

        hitbox.setPosition(
            x + HITBOX_OFFSET_X,
            y + HITBOX_OFFSET_Y
        )
        weapon.setOwnerPosition(x + WIDTH / 2f, y + HEIGHT / 2f)
        weapon.update(delta)
    }

    fun render(batch: Batch) {

        val frame = when (animationState) {
            PlayerAnimationState.IDLE ->
                idleAnimation.getKeyFrame(stateTime)

            PlayerAnimationState.RUN ->
                runAnimation.getKeyFrame(stateTime)

            PlayerAnimationState.DEATH ->
                deathAnimation.getKeyFrame(stateTime)
        }

        val drawX = MathUtils.round(x).toFloat()
        val drawY = MathUtils.round(y).toFloat()
        val drawWidth = if (facingRight) WIDTH else -WIDTH

        if (hurtTimer > 0f) {

            if ((hurtTimer * 30).toInt() % 2 == 0) {
                batch.setColor(1f, 1f, 1f, 1f)
            } else {
                batch.setColor(1f, 1f, 1f, 0.3f)
            }
        }
        println("player: $x $y")
        batch.draw(
            frame,
            if (facingRight) drawX else drawX + WIDTH,
            drawY,
            drawWidth,
            HEIGHT
        )

        batch.setColor(1f, 1f, 1f, 1f)

        weapon.render(
            batch,
            drawX + WIDTH / 2f,
            drawY + HEIGHT / 2f
        )
    }

    fun attack() {
        weapon.attack()
    }

    fun takeDamage(amount: Int) {
        health -= amount
        hurtTimer = HURT_DURATION
    }

    fun isAlive(): Boolean = health > 0

    fun isDeathAnimationFinished(): Boolean {
        return animationState == PlayerAnimationState.DEATH &&
            deathAnimation.isAnimationFinished(stateTime)
    }

    fun addXp(amount: Int): Boolean {
        xp += amount

        var leveledUp = false

        while (xp >= xpToNextLevel) {
            xp -= xpToNextLevel
            level++
            xpToNextLevel = (xpToNextLevel * 1.25f).toInt()
            leveledUp = true
        }

        return leveledUp
    }

    fun increaseMaxHealth(amount: Int) {
        maxHealth += amount
        health = maxHealth
    }

    fun increaseSpeed(multiplier: Float) {
        speed *= multiplier
    }

    private fun isBlocked(nextX: Float, nextY: Float, objects: MapObjects?): Boolean {
        if (objects == null) return false

        val testPolygon = Polygon(
            floatArrayOf(
                0f, 0f,
                HITBOX_WIDTH, 0f,
                HITBOX_WIDTH, HITBOX_HEIGHT,
                0f, HITBOX_HEIGHT
            )
        )

        testPolygon.setPosition(
            nextX + HITBOX_OFFSET_X,
            nextY + HITBOX_OFFSET_Y
        )

        for (objectMap in objects) {
            if (objectMap is PolygonMapObject) {

                if (
                    Intersector.overlapConvexPolygons(
                        testPolygon,
                        objectMap.polygon
                    )
                ) {
                    return true
                }
            }
        }

        return false
    }

}
