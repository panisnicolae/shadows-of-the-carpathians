package io.github.feykro.sotc.screens

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.utils.ScreenUtils
import io.github.feykro.sotc.MainGame
import io.github.feykro.sotc.Ritual
import io.github.feykro.sotc.entity.enemy.Enemy
import io.github.feykro.sotc.entity.enemy.EnemyFactory
import io.github.feykro.sotc.entity.enemy.EnemyManager
import io.github.feykro.sotc.entity.enemy.EnemySpawner
import io.github.feykro.sotc.entity.enemy.EnemyType
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

class GameScreen(
    game: MainGame
) : BaseScreen(game) {
    private val player = Player(Texture(Gdx.files.internal("player2.png")))
    private val direction = Vector2()
    private val map = TmxMapLoader().load("maps/map.tmx")
    private val collisionObjects = map.layers["collision"]!!.objects
    val mapWidth = map.properties["width", Int::class.java]
    val mapHeight = map.properties["height", Int::class.java]

    val tileWidth = map.properties["tilewidth", Int::class.java]
    val tileHeight = map.properties["tileheight", Int::class.java]

    val worldWidth = mapWidth * tileWidth.toFloat()
    val worldHeight = mapHeight * tileHeight.toFloat()
    private val mapRenderer = OrthogonalTiledMapRenderer(map, 1f, game.batch)
    private val enemyFactory = EnemyFactory(game.assetManager)
    private lateinit var enemySpawner: EnemySpawner
    private val projectileManager = ProjectileManager(game.assetManager.get("weapons/bullet.png", Texture::class.java))
    private val weaponFactory = WeaponFactory(game.assetManager, projectileManager)
    private lateinit var inputManager: InputManager
    private var showHitboxes = false
    private var isPaused = false
    private val hud = Hud()
    private val mouseWorldPos = Vector2()
    private var isAutoAim = true
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
        skin = Skin(
            Gdx.files.internal("ui/pixthulhu/skin/pixthulhu-ui.json")
        )
        upgradeMenu = UpgradeMenu(
            hud.stage,
            skin
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


        camera.position.set(
            viewport.worldWidth / 2f,
            viewport.worldHeight / 2f,
            0f
        )
        camera.update()

        val ritualLayer = map.layers["ritual"]

        if (ritualLayer != null) {
            for (obj in ritualLayer.objects) {
                if (obj is RectangleMapObject) {
                    val r = obj.rectangle

                    rituals += Ritual(
                        r.x,
                        r.y,
                        r.width,
                        r.height
                    )
                }
            }
        }

        enemyManager = EnemyManager(
            enemyFactory,
            player
        ) {
            isPaused = true

            upgradeMenu.show(
                UpgradeManager.randomUpgrades()
            ) { upgrade ->

                upgrade.apply(player)

                isPaused = false
            }
        }
        enemySpawner = EnemySpawner(
            enemyManager,
            viewport.worldWidth,
            viewport.worldHeight
        )
        enemySpawner.startWave(1)
        /*enemyManager.spawnEnemy(EnemyType.SKELETON, 50f, 50f)
        enemyManager.spawnEnemy(EnemyType.SKELETON, 100f, 50f)
        enemyManager.spawnEnemy(EnemyType.SKELETON, 150f, 50f)
        enemyManager.spawnEnemy(EnemyType.SKELETON, 200f, 50f)
        enemyManager.spawnEnemy(EnemyType.SKELETON, 250f, 50f)
        enemyManager.spawnEnemy(EnemyType.SKELETON, 300f, 50f)*/
        player.weapon = weaponFactory.create(weapons[currentWeapon])
    }

    override fun render(delta: Float) {

        ScreenUtils.clear(0.1f, 0.2f, 0.5f, 1f)
        viewport.apply()


        if (!isPaused) {

            val direction = inputManager.getMovement()

            player.update(
                delta,
                direction,
                worldWidth,
                worldHeight,
                collisionObjects
            )

            if (inputManager.isPressed(Action.ATTACK)) {
                player.attack()
            }

            if (inputManager.isJustPressed(Action.TOGGLE_HITBOXES)) {
                showHitboxes = !showHitboxes
            }

            if (inputManager.isJustPressed(Action.NEXT_WEAPON)) {
                nextWeapon()
            }

            enemySpawner.update(
                delta,
                player.x,
                player.y
            )

            enemyManager.update(
                delta,
                player.x,
                player.y,
                worldWidth,
                worldHeight,
                collisionObjects
            )

            projectileManager.update(
                delta,
                enemyManager.getEnemies(),
                worldWidth,
                worldHeight,
                collisionObjects
            )

            if (!player.isAlive()) {
                game.setScreen(GameOverScreen(game))
                return
            }

            if (Gdx.app.type == Application.ApplicationType.Android) {

                val enemy = enemyManager.getNearestEnemy(player.x, player.y)

                if (enemy != null &&
                    Vector2.dst(player.x, player.y, enemy.x, enemy.y) < 200f
                ) {

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

                mouseWorldPos.set(
                    Gdx.input.x.toFloat(),
                    Gdx.input.y.toFloat()
                )

                viewport.unproject(mouseWorldPos)

                val enemy = enemyManager.getNearestEnemy(player.x, player.y)

                val targetX: Float
                val targetY: Float

                if (enemy != null &&
                    Vector2.dst(player.x, player.y, enemy.x, enemy.y) < 200f
                ) {

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

            hud.setHealth(
                player.getHealth(),
                player.getMaxHealth()
            )

            hud.setXp(
                player.getXp(),
                player.getXpToNextLevel()
            )

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
        mapRenderer.render(intArrayOf(0, 1, 2))

        game.batch.use(camera.combined) {
            enemyManager.render(it)
            player.render(it)
            projectileManager.render(it)
        }

        mapRenderer.render(intArrayOf(3))

        if (showHitboxes) {
            renderHitboxes()
        }

        hud.update(delta)
        hud.render(game.batch)
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
        if (showHitboxes) {
            game.shapeRenderer.projectionMatrix = camera.combined

            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

            // Player
            val hb = player.getHitbox()
            game.shapeRenderer.rect(hb.x, hb.y, hb.width, hb.height)

            // Collisions
            for (obj in collisionObjects) {
                if (obj is RectangleMapObject) {
                    val r = obj.rectangle
                    game.shapeRenderer.rect(r.x, r.y, r.width, r.height)
                }
            }
            for (enemy in enemyManager.getEnemies()) {
                val hb = enemy.getHitbox()
                game.shapeRenderer.rect(hb.x, hb.y, hb.width, hb.height)
            }

            game.shapeRenderer.end()
        }
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
