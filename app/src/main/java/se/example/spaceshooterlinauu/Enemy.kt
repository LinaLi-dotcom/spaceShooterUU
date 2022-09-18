package se.example.spaceshooterlinauu

import android.content.res.Resources
import java.util.*
import kotlin.random.Random.Default.nextInt
const val ENEMY_HEIGHT = 60
const val ENEMY_SPAWN_OFFSET = STAGE_WIDTH * 2

class Enemy(res: Resources): BitmapEntity() {
    init {
        var id = R.drawable.enemy1
        when(rand(1, 6)) {
            1 -> {
                id = R.drawable.enemy1
            }
            2 -> {
                id = R.drawable.enemy2
            }
            3 -> {
                id = R.drawable.enemy2
            }
            4 -> {
                id = R.drawable.enemy4
            }
            5 -> {
                id = R.drawable.enemy5
            }
        }
        val bmp = loadBitmap(res, id, ENEMY_HEIGHT)
        setSprite(flipVertically(bmp))
        respawn()
    }

    fun respawn() {
        x = ((STAGE_WIDTH + RNG.nextInt(ENEMY_SPAWN_OFFSET)).toFloat())
        y = RNG.nextInt(STAGE_HEIGHT - ENEMY_HEIGHT).toFloat()
    }

    override fun update() {
        super.update()
        velX = -playerSpeed
        x += velX
        if(right() < 0 ) {
            respawn()
        }
    }

    override fun onCollision(that: Entity) {
        respawn()
    }
}

val random = Random()
private fun rand(from: Int, to: Int) : Int {
    return random.nextInt(to - from) + from
}

