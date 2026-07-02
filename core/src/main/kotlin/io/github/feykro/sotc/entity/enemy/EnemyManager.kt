package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Array

class EnemyManager(private val factory: EnemyFactory) {
    private val enemies = Array<Enemy>()

    fun spawnEnemy(type: EnemyType, x: Float, y: Float) {
        enemies.add(factory.create(type, x, y))
    }

    fun update(delta: Float, playerX: Float, playerY: Float, worldWidth: Float, worldHeight: Float) {
        for (enemy in enemies) {
            enemy.update(delta, playerX, playerY, worldWidth, worldHeight)
        }
    }

    fun render(batch: Batch) {
        for (enemy in enemies) {
            enemy.render(batch)
        }
    }
}
