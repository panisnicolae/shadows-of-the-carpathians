package io.github.feykro.sotc.input

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.Drawable

class FloatingJoystick(
    skin: Skin,
    private val radius: Float
) {

    private val center = Vector2()
    private val knob = Vector2()
    private val direction = Vector2()

    private var active = false

    private val background: Drawable
    private val knobDrawable: Drawable

    init {
        val style = skin.get(Touchpad.TouchpadStyle::class.java)

        background = style.background
        knobDrawable = style.knob
    }

    fun touchDown(x: Float, y: Float) {
        center.set(x, y)
        knob.set(x, y)
        direction.setZero()
        active = true
    }

    fun touchDragged(x: Float, y: Float) {

        if (!active) return

        knob.set(x, y)

        val distance = center.dst(knob)

        if (distance > radius) {

            knob.sub(center)
                .nor()
                .scl(radius)

            knob.add(center)
        }
    }

    fun touchUp() {
        active = false
        direction.setZero()
    }

    fun getMovement(): Vector2 {

        if (!active)
            return direction.setZero()

        direction.set(knob).sub(center)

        direction.scl(1f / radius)

        if (direction.len2() > 1f)
            direction.nor()

        return direction
    }

    fun isActive(): Boolean = active

    fun render(batch: Batch) {

        if (!active)
            return

        background.draw(
            batch,
            center.x - radius,
            center.y - radius,
            radius * 2f,
            radius * 2f
        )

        val knobRadius = radius * 0.4f

        knobDrawable.draw(
            batch,
            knob.x - knobRadius,
            knob.y - knobRadius,
            knobRadius * 2f,
            knobRadius * 2f
        )
    }
}
