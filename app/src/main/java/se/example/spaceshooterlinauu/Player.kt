package se.example.spaceshooterlinauu

import android.content.res.Resources
import android.util.Log
import androidx.core.math.MathUtils.clamp

const val PLAYER_HEIGHT = 75
const val ACCELERATION = 1.1f
const val MIN_VEL = 0.1f
const val MAX_VEL = 20f
const val GRAVITY = 1.1f
const val LIFT = - (GRAVITY *2f)
const val DRAG = 0.97f
const val PLAYER_START_HEALTH = 3

class Player(res: Resources): BitmapEntity() {
    private val TAG = "player"
    var health = PLAYER_START_HEALTH
    init {
        setSprite(loadBitmap(res, R.drawable.fish_spaceship_game_character, PLAYER_HEIGHT))
    }

    override fun onCollision(that: Entity) {
        Log.d(TAG, "onCollision")
        health--
    }

    override fun update() {
        velX *= DRAG
        velY += GRAVITY
        //slow down during to drag/friction
        if(isBoosting) {
            //accelerate
            velX *= ACCELERATION
            velY += LIFT
        }

        velX = clamp(velX, MIN_VEL, MAX_VEL)
        velY = clamp(velY, -MAX_VEL, MAX_VEL)

        y += velY
        playerSpeed = velX

        if( bottom() > STAGE_HEIGHT) {
            setBottom(STAGE_HEIGHT.toFloat())
        } else if(top() < 0f) {
            setTop(0f)
        }
    }

}