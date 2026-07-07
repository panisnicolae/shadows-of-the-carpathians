package io.github.feykro.sotc.weapons

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.melee.Axe
import io.github.feykro.sotc.weapons.melee.Sword
import io.github.feykro.sotc.weapons.projectile.ProjectileManager
import io.github.feykro.sotc.weapons.ranged.Blunderbuss
import io.github.feykro.sotc.weapons.ranged.Carbine
import io.github.feykro.sotc.weapons.ranged.Musket

class WeaponFactory(
    private val assets: AssetManager,
    private val projectileManager: ProjectileManager
) {

    fun create(type: WeaponType): Weapon {
        return when (type) {
            WeaponType.SWORD ->
                Sword(assets.get("weapons/WR_NightBoundSet _Sword_009.png", Texture::class.java))

            WeaponType.AXE ->
                Axe(assets.get("weapons/WR_NightBoundSet _Axe_002.png", Texture::class.java))

            WeaponType.CARBINE ->
                Carbine(assets.get("weapons/WR_NightBoundSet _Gun_010.png", Texture::class.java), projectileManager)

            WeaponType.BLUNDERBUSS ->
                Blunderbuss(assets.get("weapons/WR_NightBoundSet _Gun_001.png", Texture::class.java), projectileManager)

            WeaponType.MUSKET ->
                Musket(assets.get("weapons/WR_NightBoundSet _Gun_005.png", Texture::class.java), projectileManager)
        }
    }
}
