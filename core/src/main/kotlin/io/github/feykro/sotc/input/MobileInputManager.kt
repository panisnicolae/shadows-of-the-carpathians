package io.github.feykro.sotc.input

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton

class MobileInputManager(
    private val joystick: FloatingJoystick,
    private val attackButton: ImageButton
) : InputManager {

    override fun getMovement(): Vector2 =
        joystick.getMovement()

    override fun isPressed(action: Action): Boolean {

        return when (action) {
            Action.ATTACK -> attackButton.isPressed
            else -> false
        }
    }

    override fun isJustPressed(action: Action) = false
}
