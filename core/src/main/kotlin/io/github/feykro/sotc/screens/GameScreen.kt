package io.github.feykro.sotc.screens

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Color.*
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.PointMapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import io.github.feykro.sotc.MainGame
import io.github.feykro.sotc.Ritual
import io.github.feykro.sotc.entity.enemy.EnemyFactory
import io.github.feykro.sotc.entity.enemy.EnemyManager
import io.github.feykro.sotc.entity.enemy.EnemySpawner
import io.github.feykro.sotc.entity.player.Player
import io.github.feykro.sotc.input.Action
import io.github.feykro.sotc.input.DesktopInputManager
import io.github.feykro.sotc.input.InputManager
import io.github.feykro.sotc.input.MobileInputManager
import io.github.feykro.sotc.ui.hud.Hud
import io.github.feykro.sotc.ui.hud.UpgradeMenu
import io.github.feykro.sotc.upgrade.UpgradeManager
import io.github.feykro.sotc.weapons.WeaponFactory
import io.github.feykro.sotc.weapons.WeaponType
import io.github.feykro.sotc.weapons.projectile.ProjectileManager
import ktx.log.logger
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin

class GameScreen(
    game: MainGame
) : BaseScreen(game) {
    private val player = Player(game.assetManager.get("hooded.png", Texture::class.java))
    private val map = TmxMapLoader().load("maps/map2.tmx")
    private val collisionObjects = map.layers["collision"]!!.objects
    val mapWidth = map.properties["width", Int::class.java]
    val mapHeight = map.properties["height", Int::class.java]
    val tileWidth = map.properties["tilewidth", Int::class.java]
    val tileHeight = map.properties["tileheight", Int::class.java]
    val worldWidth = mapWidth * tileWidth.toFloat()
    val worldHeight = mapHeight * tileHeight.toFloat()
    private val mapRenderer = OrthogonalTiledMapRenderer(map, 1f, game.batch)
    private lateinit var enemySpawner: EnemySpawner
    private val projectileManager = ProjectileManager(
        game.assetManager.get("weapons/bullet.png", Texture::class.java),
        game.assetManager.get("weapons/projectiles.png", Texture::class.java))
    private val enemyFactory = EnemyFactory(game.assetManager,projectileManager)
    private val weaponFactory = WeaponFactory(game.assetManager, projectileManager)
    private lateinit var inputManager: InputManager
    private var showHitboxes = false
    private var isPaused = false
    private lateinit var hud: Hud
    private val mouseWorldPos = Vector2()
    private lateinit var skin: Skin
    private lateinit var upgradeMenu: UpgradeMenu
    private lateinit var enemyManager: EnemyManager
    private val rituals = mutableListOf<Ritual>()
    private val weapons = listOf(
        WeaponType.BLUNDERBUSS,
        WeaponType.MUSKET,
        WeaponType.CARBINE
    )
    private var currentWeapon = 0

    companion object {
        private val log = logger<GameScreen>()
    }

    override fun show() {
        super.show()
        log.debug { "GameScreen gets shown" }
        skin = Skin(Gdx.files.internal("ui/pixthulhu/skin/pixthulhu-ui.json"))
        Scene2DSkin.defaultSkin = skin
        hud = Hud()
        upgradeMenu = UpgradeMenu(hud.stage, skin)
        hud.createMiniMap(
            game.assetManager.get("ui/minimap.png", Texture::class.java),
            worldWidth,
            worldHeight
        )
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(hud.stage)
        Gdx.input.inputProcessor = multiplexer
        if (Gdx.app.type == Application.ApplicationType.Android) {
            hud.createMobileControls(skin)
            inputManager = MobileInputManager(
                hud.joystick,
                hud.attackButton
            )
            Gdx.input.inputProcessor = hud.stage
        } else {
            inputManager = DesktopInputManager()
        }

        camera.position.set(viewport.worldWidth / 2f, viewport.worldHeight / 2f, 0f)
        camera.update()

        for (obj in map.layers["collision"].objects) {
            if (obj is PointMapObject && obj.name == "player_spawn") {
                player.x = obj.point.x
                player.y = obj.point.y
                break
            }
        }

        val ritualLayer = map.layers["ritual"]

        if (ritualLayer != null) {
            for (obj in ritualLayer.objects) {
                if (obj is RectangleMapObject) {
                    val r = obj.rectangle
                    rituals += Ritual(r.x, r.y, r.width, r.height)
                }
            }
        }

        enemyManager = EnemyManager(enemyFactory, player) {
            isPaused = true
            upgradeMenu.show(UpgradeManager.randomUpgrades()) { upgrade ->
                upgrade.apply(player)
                isPaused = false
            }
        }
        enemySpawner = EnemySpawner(
            enemyManager,
            viewport.worldWidth,
            viewport.worldHeight,
            map.layers["spawn_collision"].objects
        )
        enemySpawner.startWave(1)
        player.weapon = weaponFactory.create(weapons[currentWeapon])
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0.1f, 0.2f, 0.5f, 1f)
        viewport.apply()

        if (!isPaused) {
            val direction = inputManager.getMovement()
            player.update(delta, direction, worldWidth, worldHeight, collisionObjects)

            if (inputManager.isPressed(Action.ATTACK)) {
                player.attack()
            }
            if (inputManager.isJustPressed(Action.TOGGLE_HITBOXES)) {
                showHitboxes = !showHitboxes
            }
            if (inputManager.isJustPressed(Action.NEXT_WEAPON)) {
                nextWeapon()
            }

            enemySpawner.update(delta, player.x, player.y)
            enemyManager.update(delta, worldWidth, worldHeight, collisionObjects)

            projectileManager.update(delta, player, enemyManager.getEnemies(), worldWidth, worldHeight, collisionObjects)

            if (player.isDeathAnimationFinished()) {
                game.setScreen(GameOverScreen(game))
                return
            }

            if (Gdx.app.type == Application.ApplicationType.Android) {
                val enemy = enemyManager.getNearestEnemy(player.x, player.y)

                if (enemy != null && Vector2.dst(player.x, player.y, enemy.x, enemy.y) < 200f) {
                    player.lookAt(enemy.x + enemy.WIDTH / 2f)
                    player.weapon.lookAt(
                        player.x + Player.WIDTH / 2f,
                        player.y + Player.HEIGHT / 2f,
                        enemy.x + enemy.WIDTH / 2f,
                        enemy.y + enemy.HEIGHT / 2f
                    )
                } else {
                    val move = inputManager.getMovement()
                    if (!move.isZero) {
                        player.lookAt(player.x + move.x)
                        player.weapon.lookAt(
                            player.x + Player.WIDTH / 2f,
                            player.y + Player.HEIGHT / 2f,
                            player.x + move.x * 100f,
                            player.y + move.y * 100f
                        )
                    }
                }
            } else {
                mouseWorldPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
                viewport.unproject(mouseWorldPos)

                val enemy = enemyManager.getNearestEnemy(player.x, player.y)

                val targetX: Float
                val targetY: Float

                if (enemy != null && Vector2.dst(player.x, player.y, enemy.x, enemy.y) < 200f) {
                    targetX = enemy.x + enemy.WIDTH / 2f
                    targetY = enemy.y + enemy.HEIGHT / 2f
                } else {
                    targetX = mouseWorldPos.x
                    targetY = mouseWorldPos.y
                }

                player.lookAt(targetX)
                player.weapon.lookAt(
                    player.x + Player.WIDTH / 2f,
                    player.y + Player.HEIGHT / 2f,
                    targetX,
                    targetY
                )
            }
            hud.setHealth(player.getHealth(), player.getMaxHealth())
            hud.setXp(player.getXp(), player.getXpToNextLevel())
            hud.setLevel(player.getLevel())
            hud.setKills(enemyManager.getKills())
        }

        //camera
        val halfWidth = viewport.worldWidth / 2f
        val halfHeight = viewport.worldHeight / 2f

        camera.position.set(
            MathUtils.clamp(
                player.x + Player.WIDTH / 2f,
                halfWidth,
                worldWidth - halfWidth
            ),
            MathUtils.clamp(
                player.y + Player.HEIGHT / 2f,
                halfHeight,
                worldHeight - halfHeight
            ),
            0f
        )
        camera.update()

        //render
        mapRenderer.setView(camera)
        mapRenderer.render(intArrayOf(0, 1, 3))

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        game.shapeRenderer.use(ShapeRenderer.ShapeType.Filled, camera.combined) {
            it.color = Color(0.2f, 0.2f, 0.2f, 0.4f)
            it.ellipse(player.x + 8f, player.y -3f, 16f, 6f)
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)

        game.batch.use(camera.combined) {
            enemyManager.render(it)
            player.render(it)
            projectileManager.render(it)
        }

        mapRenderer.render(intArrayOf(2, 4))

        if (showHitboxes) {
            renderHitboxes()
        }

        hud.update(delta)
        hud.render(game.batch, game.shapeRenderer, player, rituals)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        hud.resize(width, height)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        hud.dispose()
    }

    fun renderHitboxes() {
        if (!showHitboxes) return

        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.color.set(WHITE)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        // Player
        game.shapeRenderer.polygon(player.getHitbox().transformedVertices)

        //Projectiles
        projectileManager.renderHitboxes(game.shapeRenderer)

        // map objects
        for (obj in collisionObjects) {
            if (obj is PolygonMapObject) {
                game.shapeRenderer.polygon(
                    obj.polygon.transformedVertices
                )
            }
        }

        // enemies
        for (enemy in enemyManager.getEnemies()) {
            game.shapeRenderer.polygon(
                enemy.getHitbox().transformedVertices
            )
        }
        game.shapeRenderer.end()
    }

    private fun nextWeapon() {
        currentWeapon++

        if (currentWeapon >= weapons.size)
            currentWeapon = 0

        player.weapon = weaponFactory.create(
            weapons[currentWeapon]
        )
    }
}
