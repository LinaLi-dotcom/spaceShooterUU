package se.example.spaceshooterlinauu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager

class GameActivity : AppCompatActivity() {
    private val TAG = "GameActivity"
    lateinit var game : Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        game = Game(this)
        setContentView(game)
        Log.d(TAG, "onCreate called")
    }

    override fun onPause() {
        game.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        game.resume()
    }


}