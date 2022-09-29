package se.example.spaceshooterlinauu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import se.example.spaceshooterlinauu.Game.Companion.LONGEST_DIST
import se.example.spaceshooterlinauu.Game.Companion.PREFS

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button= findViewById<Button>(R.id.buttonStart)
        button?.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val longestDistance = prefs.getInt(LONGEST_DIST, 0)
        val highScore = findViewById<TextView>(R.id.highScore)
        highScore.text = "Longest distance: $longestDistance km"
    }
}