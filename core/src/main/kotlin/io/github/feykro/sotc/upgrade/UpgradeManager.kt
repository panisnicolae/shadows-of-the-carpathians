package io.github.feykro.sotc.upgrade

object UpgradeManager {

    private val upgrades = listOf(
        WeaponUpgrade(),
        HealthUpgrade(),
        SpeedUpgrade()
    )

    fun randomUpgrades(): List<Upgrade> {
        return upgrades.shuffled().take(3)
    }
}
