package io.github.feykro.sotc

class WaveManager(
    private val maxWave: Int = 4
) {

    var currentWave = 1
        private set

    fun nextWave() {
        if (currentWave < maxWave) {
            currentWave++
        }
    }

    fun requiredKills(): Int {
        return currentWave * 3
    }

    fun isLastWave(): Boolean {
        return currentWave == maxWave
    }

    fun isBossWave(): Boolean {
        return currentWave > maxWave
    }

    fun reset() {
        currentWave = 1
    }
}
