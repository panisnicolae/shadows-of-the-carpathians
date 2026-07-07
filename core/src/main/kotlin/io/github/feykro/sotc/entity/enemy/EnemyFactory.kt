package io.github.feykro.sotc.entity.enemy

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture

enum class EnemyType {
    STRIGOI,
    SKELETON
}
class EnemyFactory(
    private val assets: AssetManager
) {

    fun create(type: EnemyType, x: Float, y: Float): Enemy =
        when(type) {
            EnemyType.STRIGOI -> Strigoi(x, y, assets["player.png", Texture::class.java])
            EnemyType.SKELETON -> Skeleton(x, y, assets["enemies/Skeleton enemy.png", Texture::class.java])
        }
}
