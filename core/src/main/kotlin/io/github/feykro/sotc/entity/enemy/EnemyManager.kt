package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

class EnemyManager(private val factory: EnemyFactory) {
    private val enemies = Array<Enemy>()

    fun spawnEnemy(type: EnemyType, x: Float, y: Float) {
        enemies.add(factory.create(type, x, y))
    }

    fun update(delta: Float, playerX: Float, playerY: Float, worldWidth: Float, worldHeight: Float) {
        val iterator = enemies.iterator()
        while (iterator.hasNext()) {
            val enemy = iterator.next()
            enemy.update(delta, playerX, playerY, worldWidth, worldHeight)
            if (!enemy.isAlive()) {
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
            val distance = Vector2.dst2(
                playerX,
                playerY,
                enemy.x,
                enemy.y
            )

            if (distance < minDistance) {
                minDistance = distance
                nearest = enemy
            }
        }

        return nearest
    }
}
