package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class EnemySpawner(
    private val enemyManager: EnemyManager,
    private val cameraWidth: Float,
    private val cameraHeight: Float
) {

    private var spawning = false
    private var currentWave = 1

    private var spawnTimer = 0f
    private var spawnInterval = 3f

    private fun maxAliveEnemies(): Int =
        5 + currentWave * 2

    fun startWave(wave: Int) {
        currentWave = wave
        spawning = true
        spawnTimer = 0f
    }

    fun stopWave() {
        spawning = false
    }

    fun update(
        delta: Float,
        playerX: Float,
        playerY: Float
    ) {

        if (!spawning)
            return

        if (enemyManager.getEnemies().size >= maxAliveEnemies())
            return

        spawnTimer += delta

        if (spawnTimer >= spawnInterval) {

            spawnTimer = 0f

            spawnEnemy(playerX, playerY)
        }
    }

    private fun spawnEnemy(
        playerX: Float,
        playerY: Float
    ) {

        val pos = randomSpawnOutsideCamera(
            playerX,
            playerY
        )

        enemyManager.spawnEnemy(
            enemyType(),
            pos.x,
            pos.y
        )
    }

    private fun enemyType(): EnemyType {

        return when {

            currentWave <= 2 ->
                EnemyType.SKELETON

            else ->
                EnemyType.SKELETON
        }
    }

    private fun randomSpawnOutsideCamera(
        playerX: Float,
        playerY: Float
    ): Vector2 {

        val halfW = cameraWidth / 2f
        val halfH = cameraHeight / 2f

        val margin = 150f

        return when (MathUtils.random(3)) {

            0 -> Vector2(
                playerX - halfW - margin,
                playerY + MathUtils.random(-halfH, halfH)
            )

            1 -> Vector2(
                playerX + halfW + margin,
                playerY + MathUtils.random(-halfH, halfH)
            )

            2 -> Vector2(
                playerX + MathUtils.random(-halfW, halfW),
                playerY + halfH + margin
            )

            else -> Vector2(
                playerX + MathUtils.random(-halfW, halfW),
                playerY - halfH - margin
            )
        }
    }
}
