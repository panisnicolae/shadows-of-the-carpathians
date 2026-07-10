package io.github.feykro.sotc.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2


class DesktopInputManager : InputManager {

    private val keyBindings = mapOf(
        Action.MOVE_UP to Input.Keys.W,
        Action.MOVE_DOWN to Input.Keys.S,
        Action.MOVE_LEFT to Input.Keys.A,
        Action.MOVE_RIGHT to Input.Keys.D,

        Action.INTERACT to Input.Keys.E,
        Action.PAUSE to Input.Keys.ESCAPE,

        Action.TOGGLE_HITBOXES to Input.Keys.F3,
        Action.NEXT_WEAPON to Input.Keys.F4
    )

    private val mouseBindings = mapOf(
        Action.ATTACK to Input.Buttons.LEFT
    )

    override fun isPressed(action: Action): Boolean {

        keyBindings[action]?.let {
            return Gdx.input.isKeyPressed(it)
        }

        mouseBindings[action]?.let {
            return Gdx.input.isButtonPressed(it)
        }

        return false
    }

    override fun isJustPressed(action: Action): Boolean {

        keyBindings[action]?.let {
            return Gdx.input.isKeyJustPressed(it)
        }

        mouseBindings[action]?.let {
            return Gdx.input.isButtonJustPressed(it)
        }

        return false
    }

    override fun getMovement(): Vector2 {

        val direction = Vector2()

        if (isPressed(Action.MOVE_UP))
            direction.y++

        if (isPressed(Action.MOVE_DOWN))
            direction.y--

        if (isPressed(Action.MOVE_LEFT))
            direction.x--

        if (isPressed(Action.MOVE_RIGHT))
            direction.x++

        return direction.nor()
    }
}
