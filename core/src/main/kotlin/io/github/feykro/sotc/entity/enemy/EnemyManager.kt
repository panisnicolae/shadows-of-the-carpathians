package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import io.github.feykro.sotc.entity.player.Player

class EnemyManager(
    private val factory: EnemyFactory,
    private val player: Player,
    private val onLevelUp: () -> Unit
) {
    private val enemies = Array<Enemy>()
    private var kills = 0

    fun getKills() = kills

    fun spawnEnemy(type: EnemyType, x: Float, y: Float) {
        val enemy = factory.create(type, x, y)

        if (enemy is Zmeu) {
            // 1. Adăugăm segmentele de la COADĂ spre GÂT (12 -> 1)
            // Astfel, segmentul 12 va fi cel mai jos, iar segmentul 1 va fi sub cap.
            for (i in 12 downTo 1) {
                val segment = ZmeuSegment(x, y, enemy.bodyTex, enemy, i)
                enemies.add(segment)
            }
        }
        enemies.add(enemy)
    }

    fun update(delta: Float, worldWidth: Float, worldHeight: Float,collisionObjects: MapObjects?) {
        val iterator = enemies.iterator()
        while (iterator.hasNext()) {
            val enemy = iterator.next()
            enemy.update(delta, player, worldWidth, worldHeight,collisionObjects)

            if (enemy.canBeRemoved()) {
                kills++
                if (player.addXp(enemy.xpReward)) {
                    Gdx.app.log("LEVEL", "callback")
                    onLevelUp()
                }
                iterator.remove()
            }
        }
    }

    fun getEnemies(): Array<Enemy> = enemies

    fun render(batch: Batch) {
        for (enemy in enemies) {
            enemy.render(batch)
        }
    }

    fun getNearestEnemy(playerX: Float, playerY: Float): Enemy? {
        var nearest: Enemy? = null
        var minDistance = Float.MAX_VALUE

        for (enemy in enemies) {
            if (!enemy.canBeHit())
                continue
            val distance = Vector2.dst2(
                playerX + Player.WIDTH / 2f,
                playerY + Player.HEIGHT / 2f,
                enemy.x + enemy.WIDTH / 2f,
                enemy.y + enemy.HEIGHT / 2f
            )

            if (distance < minDistance) {
                minDistance = distance
                nearest = enemy
            }
        }

        return nearest
    }
}
