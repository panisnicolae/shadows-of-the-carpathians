package io.github.feykro.sotc.weapons.projectile

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import io.github.feykro.sotc.entity.enemy.Enemy
import io.github.feykro.sotc.entity.player.Player

class ProjectileManager(

    bulletTexture: Texture,
    magicProjectileTexture: Texture

) {
    private val bulletPool = ProjectilePool {
        Bullet(bulletTexture)
    }
    private val magicPool = ProjectilePool {
        MagicProjectile(magicProjectileTexture)
    }


    private val activeProjectiles = Array<Projectile>()

    fun spawnBullet(
        x: Float,
        y: Float,
        direction: Vector2,
        speed: Float,
        damage: Int
    ) {

        val bullet = bulletPool.obtain()
        bullet.owner = ProjectileOwner.PLAYER

        bullet.spawn(
            x,
            y,
            direction,
            speed,
            damage
        )

        activeProjectiles.add(bullet)
    }

    fun spawnMagic(
        x: Float,
        y: Float,
        direction: Vector2,
        speed: Float,
        damage: Int
    ) {

        val projectile = magicPool.obtain()
        projectile.owner = ProjectileOwner.ENEMY
        projectile.spawn(
            x,
            y,
            direction,
            speed,
            damage
        )

        activeProjectiles.add(projectile)
    }

    fun update(delta: Float, player: Player, enemies: Array<Enemy>, worldWidth: Float, worldHeight: Float, collisionObjects: MapObjects?) {

        var i = activeProjectiles.size - 1

        while (i >= 0) {

            val projectile = activeProjectiles[i]

            projectile.update(delta)

            // Distrugere dacă iese din limitele hărții
            if (projectile.x < 0 || projectile.x > worldWidth || projectile.y < 0 || projectile.y > worldHeight) {
                projectile.destroy()
            }

            // Coliziune cu inamicii
            if (projectile.isActive()) {

                when (projectile.owner) {

                    ProjectileOwner.PLAYER -> {

                        for (enemy in enemies) {
                            if (
                                enemy.canBeHit() &&
                                Intersector.overlapConvexPolygons(
                                    projectile.getHitbox(),
                                    enemy.getHitbox()
                                )
                            ) {
                                enemy.takeDamage(projectile.damage)
                                projectile.destroy()
                                break
                            }
                        }
                    }

                    ProjectileOwner.ENEMY -> {
                        if (
                            player.isAlive() &&
                            Intersector.overlapConvexPolygons(
                                projectile.getHitbox(),
                                player.getHitbox()
                            )
                        ) {
                            player.takeDamage(projectile.damage)
                            projectile.destroy()
                        }
                    }
                }
            }

            // Coliziune cu obiectele hărții (ziduri, copaci)
            if (projectile.isActive() && collisionObjects != null) {
                for (obj in collisionObjects) {
                    if (obj is PolygonMapObject) {
                        if (
                            Intersector.overlapConvexPolygons(
                                projectile.getHitbox(),
                                obj.polygon
                            )
                        ) {
                            projectile.destroy()
                            break
                        }
                    }
                }
            }

            if (!projectile.isActive()) {
                activeProjectiles.removeIndex(i)
                when (projectile) {
                    is Bullet -> bulletPool.free(projectile)
                    is MagicProjectile -> magicPool.free(projectile)
                }
            }

            i--
        }
    }

    fun render(batch: Batch) {

        for (projectile in activeProjectiles) {
            projectile.render(batch)
        }
    }

    fun renderHitboxes(shapeRenderer: ShapeRenderer) {
        for (projectile in activeProjectiles) {
            shapeRenderer.polygon(projectile.getHitbox().transformedVertices)
        }
    }
}
