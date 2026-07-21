package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import io.github.feykro.sotc.entity.player.Player

class Zmeu(
    x: Float,
    y: Float,
    headTex: Texture,
    headTopTex: Texture,
    val bodyTex: Texture
) : Enemy(x, y, headTex) {

    override val WIDTH = 64f
    override val HEIGHT = 64f
    override val speed = 50f
    override val xpReward = 1500

    // 1. Regiuni pentru Cap (Side-View) - Originalul privește spre STÂNGA
    private val headLeft = TextureRegion(headTex)
    private val headRight = TextureRegion(headTex).apply { flip(true, false) }

    // 2. Regiune pentru Cap (Top-View) - Originalul tău (head_1_top) privește în JOS
    private val headTop = TextureRegion(headTopTex)

    // 3. Regiune pentru Corp - Corpul tău (corp_12) este Top-Down și privește în JOS
    private val bodyRegion = TextureRegion(bodyTex)

    private val segments = 12
    val history = mutableListOf<Vector2>()
    private val bodySegmentsPos = Array(segments) { Vector2(x, y) }
    private val bodySegmentsRotation = FloatArray(segments)

    init {
        health = 5000
        setupHitbox()
    }

    override fun update(delta: Float, player: Player, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {
        super.update(delta, player, worldWidth, worldHeight, collisionObjects)

        // Adăugăm puncte în istoric mult mai des (la fiecare 5 pixeli parcurși)
        // Asta va face corpul mult mai compact și legat.
        if (history.isEmpty() || history.first().dst(x, y) > 5f) {
            history.add(0, Vector2(x, y))
            if (history.size > segments * 15) history.removeAt(history.size - 1)
        }

        for (i in 0 until segments) {
            // Indexul în istoric - am scăzut de la 6 la 4 pentru a "lipi" segmentele
            val targetIndex = (i + 1) * 4

            if (targetIndex < history.size) {
                val targetPos = history[targetIndex]

                // Lerp mai rapid (0.4f) pentru ca segmentele să nu rămână în urmă
                bodySegmentsPos[i].lerp(targetPos, 0.4f)

                val prevPos = if (i == 0) Vector2(x, y) else bodySegmentsPos[i - 1]

                // Calculăm rotația exactă față de piesa din față
                bodySegmentsRotation[i] = MathUtils.atan2(
                    prevPos.y - bodySegmentsPos[i].y,
                    prevPos.x - bodySegmentsPos[i].x
                ) * MathUtils.radiansToDegrees
            }
        }
    }

    // În Zmeu.kt
    override fun render(batch: Batch) {
        // ȘTERGEM bucla "for (i in segments - 1 downTo 0)" care randa corpul!

        // Randăm doar Capul
        val headAngle = direction.angleDeg()
        val isVertical = headAngle in 45f..135f || headAngle in 225f..315f

        if (isVertical) {
            batch.draw(headTop, x, y, WIDTH / 2f, HEIGHT / 2f, WIDTH, HEIGHT, 1f, 1f, headAngle + 90f)
        } else {
            val isLeft = headAngle > 90 && headAngle < 270
            val region = if (isLeft) headLeft else headRight
            val finalRot = if (isLeft) headAngle + 180f else headAngle
            batch.draw(region, x, y, WIDTH / 2f, HEIGHT / 2f, WIDTH, HEIGHT, 1f, 1f, finalRot)
        }
    }
}

class ZmeuSegment(
    x: Float,
    y: Float,
    texture: Texture,
    private val head: Zmeu,
    val segmentIndex: Int
) : Enemy(x, y, texture) {

    override val WIDTH = 64f
    override val HEIGHT = 64f
    override val speed = 0f // Nu se mișcă singur
    override val xpReward = 100
    private var rotation = 0f
    override val countsTowardsLimit = false

    init {
        health = 800
        setupHitbox()
    }

    override fun update(delta: Float, player: Player, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {
        // Luăm poziția din istoricul capului
        val targetIdx = segmentIndex * 4
        if (targetIdx < head.history.size) {
            val targetPos = head.history[targetIdx]

            // --- SOLUȚIA PENTRU FPS MIC: LERP ---
            // În loc de x = targetPos.x (care e o săritură),
            // ne mișcăm progresiv spre țintă în funcție de delta time.
            // 15f este viteza de interpolare - poți pune 20f dacă vrei să fie mai "rigid"
            this.x = MathUtils.lerp(this.x, targetPos.x, delta * 15f)
            this.y = MathUtils.lerp(this.y, targetPos.y, delta * 15f)

            // Calculăm rotația față de piesa din față (rămâne la fel)
            val prevPos = if (segmentIndex == 1) Vector2(head.x, head.y) else head.history[(segmentIndex - 1) * 4]

            // Folosim MathUtils.atan2 din LibGDX
            val targetRotation = MathUtils.atan2(prevPos.y - y, prevPos.x - x) * MathUtils.radiansToDegrees

            // Putem interpola și rotația pentru extra-smoothness!
            this.rotation = MathUtils.lerpAngleDeg(this.rotation, targetRotation, delta * 10f)
        }
    }

    override fun render(batch: Batch) {
        // Folosim logica de randare cu rotație + 180 (pentru orientarea solzilor)
        batch.draw(TextureRegion(texture), x, y, WIDTH / 2f, HEIGHT / 2f, WIDTH, HEIGHT, 1f, 1f, rotation + 180f)
    }

    // Dacă capul moare, și segmentele ar trebui să dispară (opțional)
    override fun canBeRemoved(): Boolean {
        return super.canBeRemoved() || !head.isAlive()
    }
}
