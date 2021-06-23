package de.htwberlin.learningcompanion.userinterface.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()
        handler.postDelayed({
            getMainIntent()
        }, 1500)
    }

    private fun getMainIntent() {
        startActivity(Intent(this, TutorialActivity::class.java).apply {
            putExtras(TutorialActivityArgs(false).toBundle())
        })
    }
}
