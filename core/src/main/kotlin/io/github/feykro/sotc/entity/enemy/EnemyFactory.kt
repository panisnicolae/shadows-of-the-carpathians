package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import io.github.feykro.sotc.weapons.projectile.ProjectileManager

enum class EnemyType {
    STRIGOI,
    SKELETON,
    NECROMANCER
}
class EnemyFactory(
    private val assets: AssetManager,
    private val projectileManager: ProjectileManager
) {

    fun create(type: EnemyType, x: Float, y: Float): Enemy =
        when(type) {
            EnemyType.STRIGOI -> Strigoi(x, y, assets["player.png", Texture::class.java])
            EnemyType.SKELETON -> Skeleton(x, y, assets["enemies/Skeleton enemy.png", Texture::class.java])
            EnemyType.NECROMANCER -> Necromancer(x, y,
                assets["enemies/Necromancer/Idle/spr_NecromancerIdle_strip50.png", Texture::class.java],
                assets["enemies/Necromancer/Walk/spr_NecromancerWalk_strip10.png", Texture::class.java],
                assets["enemies/Necromancer/Attack/spr_NecromancerAttackWithoutEffect_strip47.png", Texture::class.java],
                assets["enemies/Necromancer/Death/spr_NecromancerDeath_strip52.png", Texture::class.java],
                assets["enemies/Necromancer/GetHit/spr_NecromancerGetHit_strip9.png", Texture::class.java],
                projectileManager
            )
        }
}
