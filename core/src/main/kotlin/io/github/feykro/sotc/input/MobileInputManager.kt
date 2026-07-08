package io.github.feykro.sotc.input

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad

class MobileInputManager(
    private val movePad: Touchpad,
    private val attackButton: ImageButton
) : InputManager {

    override fun getMovement(): Vector2 =
        Vector2(
            movePad.knobPercentX,
            movePad.knobPercentY
        )

    override fun isPressed(action: Action): Boolean {

        return when(action) {
            Action.ATTACK -> attackButton.isPressed
            else -> false
        }
    }

    override fun isJustPressed(action: Action) = false
}
