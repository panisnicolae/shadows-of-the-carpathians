package io.github.feykro.sotc.weapons

import com.badlogic.gdx.graphics.Texture

class Sword(texture: Texture) : MeleeWeapon(
    texture,
    damage = 20,
    attackSpeed = 0.3f
)
