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

const val STAGE_WIDTH = 1080
const val STAGE_HEIGHT = 720
const val STAR_COUNT = 40
const val ENEMY_COUNT = 10
val RNG = Random(uptimeMillis())
@Volatile var isBoosting = false
var playerSpeed = 0f
private val TAG = "game"

class Game(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback{
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val editor = prefs.edit()
    private lateinit var gameThread : Thread

    @Volatile
    private var isRunning = false
    private var isGameOver = false
    private val jukebox = Jukebox(context.assets)
    private val player = Player(resources)
    private val entities = ArrayList<Entity>()
    private val paint = Paint()
    private var distanceTraveled = 0
    private var maxDistanceTraveled = 0


    init{
        resources
        holder.addCallback(this)
        holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT)
        for (i in 0 until STAR_COUNT) {
            entities.add(Star())
        }
        for (i in 0 until ENEMY_COUNT) {
            entities.add(Enemy(resources))
        }
    }

    private fun restart() {
        // reset all entities
        for(entity in entities) {
            entity.respawn()
        }
        player.respawn()
        distanceTraveled = 0
        maxDistanceTraveled = prefs.getInt(LONGEST_DIST, 0)
        isGameOver = false
    }

    override fun run() {
        while (isRunning) {
            update()
            render()
        }
    }

    private fun update(){
        player.update()
        for(entity in entities){
            entity.update()
        }
        distanceTraveled += playerSpeed.toInt()
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
            canvas.drawText("Distance: $distanceTraveled", margin, textSize*2, paint)
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
            isGameOver = true
            if(distanceTraveled > maxDistanceTraveled) {
                editor.putInt(LONGEST_DIST, distanceTraveled)
                editor.apply()
            }
        }
    }

    private fun checkCollisions() {
        for(i in STAR_COUNT until entities.size) {
            val enemy = entities[i]
            if(isColliding(enemy, player)) {
                enemy.onCollision(player)
                player.onCollision(enemy)
                jukebox.play(SFX.crash)
            }
        }
    }

    private fun render() {
        val canvas = acquireAndLockCanvas() ?: return
        //lock and acquire surface
        //draw the game world to the surface
        //unlock and post the surface to the UI thread
        canvas.drawColor(Color.BLUE)
        for(entity in entities){
            entity.render(canvas, paint)
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
        catch (_: Exception) {

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
                if(isGameOver){
                    restart()
                } else {
                    isBoosting = true
                }
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

    companion object {
        const val PREFS = "se.example.spaceshooterlinauu"
        const val LONGEST_DIST = "longest_distance"
    }
}