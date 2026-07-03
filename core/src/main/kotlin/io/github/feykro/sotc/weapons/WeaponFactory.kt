package io.github.feykro.sotc.weapons

import com.badlogic.gdx.assets.AssetManager

class WeaponFactory(
    private val assets: AssetManager
) {

    fun create(type: WeaponType): Weapon {
        return when(type) {
            WeaponType.SWORD ->
                Sword(assets["weapons/WR_NightBoundSet _Sword_009.png"])

            WeaponType.AXE ->
                Axe(assets["weapons/WR_NightBoundSet _Axe_002.png"])
            else ->
                error("Weapon not implemented")
        }
    }
}
