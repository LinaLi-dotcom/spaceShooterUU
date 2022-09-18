package se.example.spaceshooterlinauu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock.uptimeMillis
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

const val STAGE_WIDTH = 1080
const val STAGE_HEIGHT = 720
const val STAR_COUNT = 40
const val ENEMY_COUNT = 10
public val RNG = Random(uptimeMillis())
@Volatile var isBoosting = false
var playerSpeed = 0f

class Game(context: Context?) : SurfaceView(context), Runnable, SurfaceHolder.Callback{
    private val TAG = "game"
    private lateinit var gameThread : Thread

    @Volatile
    private var isRunning = false
    private var isGameOver = false
    private val player = Player(resources)
    private val stars = ArrayList<Entity>()
    private val enemies = ArrayList<Entity>()
    private val paint = Paint()
    private var distanceTraveled = 0f

    init{
        resources
        holder.addCallback(this)
        holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT)
        for (i in 0 until STAR_COUNT) {
            stars.add(Star())
        }
        for (i in 0 until ENEMY_COUNT) {
            enemies.add(Enemy(resources))
        }
    }
    override fun run() {
        while (isRunning) {
            update()
            render()
        }
    }

    private fun update(){
        player.update()
        for(star in stars){
            star.update()
        }
        for(enemy in enemies){
            enemy.update()
        }
        distanceTraveled += playerSpeed
        checkCollisions()
        checkGameOver()
    }

    private fun renderHud(canvas: Canvas, paint: Paint) {
        val textSize = 48f
        val margin = 20f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = textSize
        if(!isGameOver) {
            canvas.drawText("Health: ${ player.health}", margin, textSize, paint)
            canvas.drawText("Distance: ${ distanceTraveled.roundToInt() }", margin, textSize*2, paint)
        } else {
            paint.textAlign = Paint.Align.CENTER
            val centerX = STAGE_WIDTH * 0.5f
            val centerY = STAGE_HEIGHT * 0.5f
            canvas.drawText("Game Over!", centerX, centerY, paint)
            canvas.drawText("(press to restart)", centerX, centerY+textSize, paint)
        }

    }

    private fun checkGameOver() {
        if(player.health < 0) {
            // do some game over stuff
            isGameOver = true
        }
    }

    private fun checkCollisions() {
        for(enemy in enemies) {
            if(isColliding(enemy, player)) {
                enemy.onCollision(player)
                player.onCollision(enemy)
                //TODO: play sound effects
            }
        }
    }

    private fun render() {
        val canvas = acquireAndLockCanvas() ?: return
        //lock and acquire surface
        //draw the game world to the surface
        //unlock and post the surface to the UI thread
        canvas.drawColor(Color.BLUE)
        for(star in stars){
            star.render(canvas, paint)
        }
        for(enemy in enemies){
            enemy.render(canvas, paint)
        }
        player.render(canvas, paint)
        renderHud(canvas, paint)
        holder.unlockCanvasAndPost(canvas)
    }

    private fun acquireAndLockCanvas() : Canvas? {
        if(holder?.surface?.isValid == false){
            return null
        }
        return holder.lockCanvas()
    }

    fun pause() {
        isRunning = false
        Log.d(TAG, "pause")
        //join thread, stop all work
        try {
            gameThread.join()
        }
        catch (e: Exception) {

        }
    }

    fun resume() {
        isRunning = true
        Log.d(TAG, "resume")
        //create thread and start the work
        gameThread = Thread(this)
        gameThread.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "slowing down")
                isBoosting = false}
            MotionEvent.ACTION_DOWN ->{
                Log.d(TAG, "isBoosting")
                isBoosting = true
            }
        }
        return true
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated")
    }

    override fun surfaceChanged(p0: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged, width:$width, height:$height")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed")
    }
}