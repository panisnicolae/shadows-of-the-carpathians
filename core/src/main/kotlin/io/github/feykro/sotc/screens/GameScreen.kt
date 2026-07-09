package io.github.feykro.sotc.screens

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.utils.ScreenUtils
import io.github.feykro.sotc.MainGame
import io.github.feykro.sotc.entity.enemy.EnemyFactory
import io.github.feykro.sotc.entity.enemy.EnemyManager
import io.github.feykro.sotc.entity.enemy.EnemyType
import io.github.feykro.sotc.entity.player.Player
import io.github.feykro.sotc.input.Action
import io.github.feykro.sotc.input.DesktopInputManager
import io.github.feykro.sotc.input.InputManager
import io.github.feykro.sotc.input.MobileInputManager
import io.github.feykro.sotc.ui.hud.Hud
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
    private val enemyManager = EnemyManager(enemyFactory,player)
    private val projectileManager = ProjectileManager(game.assetManager.get("weapons/bullet.png", Texture::class.java))
    private val weaponFactory = WeaponFactory(game.assetManager, projectileManager)
    private lateinit var inputManager: InputManager
    private var showHitboxes = false
    private val hud = Hud()
    private val mouseWorldPos = Vector2()
    private var isAutoAim = true

    companion object {
        private val log = logger<GameScreen>()
    }

    override fun show() {
        super.show()
        log.debug { "GameScreen gets shown" }

        if (Gdx.app.type == Application.ApplicationType.Android) {

            val skin = Skin(Gdx.files.internal("ui/pixthulhu/skin/pixthulhu-ui.json"))

            hud.createMobileControls(skin)

            inputManager = MobileInputManager(
                hud.movePad,
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
        enemyManager.spawnEnemy(
            EnemyType.SKELETON,
            20f,
            20f
        )
        player.weapon = weaponFactory.create(WeaponType.CARBINE)
    }

    override fun render(delta: Float) {

        viewport.apply()
        val direction = inputManager.getMovement()

        player.update(delta, direction, worldWidth, worldHeight, collisionObjects)

        if (inputManager.isPressed(Action.ATTACK)) {
            player.attack()
        }

        if (inputManager.isJustPressed(Action.TOGGLE_HITBOXES)) {
            showHitboxes = !showHitboxes
        }

        ScreenUtils.clear(0.1f, 0.2f, 0.5f, 1.0f)

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

        mapRenderer.setView(camera)
        mapRenderer.render(intArrayOf(0,1,2))

        enemyManager.update(delta, player.x, player.y, worldWidth, worldHeight, collisionObjects)
        projectileManager.update(delta, enemyManager.getEnemies(), worldWidth, worldHeight, collisionObjects)

        if (!player.isAlive()) {
            game.setScreen(GameOverScreen(game))
            return
        }

        if (Gdx.app.type == Application.ApplicationType.Android) {

            val enemy = enemyManager.getNearestEnemy(player.x, player.y)

            if (enemy != null &&
                Vector2.dst(player.x, player.y, enemy.x, enemy.y) < 200f) {

                player.lookAt(enemy.x)
                player.weapon.lookAt(
                    player.x + Player.WIDTH / 2f,
                    player.y + Player.HEIGHT / 2f,
                    enemy.x + 16f,
                    enemy.y + 16f
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

            if (enemy != null &&
                Vector2.dst(player.x, player.y, enemy.x, enemy.y) < 200f) {

                targetX = enemy.x + 16f
                targetY = enemy.y + 16f

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

        game.batch.use(camera.combined) {
            enemyManager.render(it)
            player.render(it)
            projectileManager.render(it)
        }
        mapRenderer.render(intArrayOf(3))

        if (showHitboxes) {
            renderHitboxes()
        }
        hud.setHealth(
            player.getHealth(),
            player.getMaxHealth()
        )

        /*hud.setXp(
            player.getXp(),
            player.getMaxXp()
        )*/

        hud.update(delta)
        hud.render()

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
}
