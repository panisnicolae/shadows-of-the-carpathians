package io.github.feykro.sotc.upgrade

import io.github.feykro.sotc.entity.player.Player

interface Upgrade {
    val title: String
    val description: String
    fun apply(player: Player)
}

class WeaponUpgrade : Upgrade {
    override val title = "Putere Militara"
    override val description = "Creste nivelul armei curente."
    override fun apply(player: Player) {
        player.weapon.upgrade()
    }
}

class HealthUpgrade : Upgrade {
    override val title = "Vitalitate"
    override val description = "Creste viata maxima cu 20 puncte."
    override fun apply(player: Player) {
        player.increaseMaxHealth(20)
    }
}

class SpeedUpgrade : Upgrade {
    override val title = "Agilitate"
    override val description = "Creste viteza de miscare cu 10%."
    override fun apply(player: Player) {
        player.increaseSpeed(1.1f)
    }
}
