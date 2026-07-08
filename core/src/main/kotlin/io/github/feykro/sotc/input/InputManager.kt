package io.github.feykro.sotc.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2

interface InputManager {

    fun isPressed(action: Action): Boolean

    fun isJustPressed(action: Action): Boolean

    fun getMovement(): Vector2


}
