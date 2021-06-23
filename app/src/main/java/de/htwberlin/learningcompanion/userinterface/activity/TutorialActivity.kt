package de.htwberlin.learningcompanion.userinterface.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import kotlinx.android.synthetic.main.activity_tutorial.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class TutorialActivity : AppCompatActivity() {

    private val args: TutorialActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        btnSkippTutorial.setOnClickListener {
            alert(R.string.tutorial_skipdialog_text, R.string.tutorial_skipdialog_title) {
                yesButton {
                    SharedPreferencesHelper.get(this@TutorialActivity).setFirstStart(false)
                    launchMainActivity()
                }
                noButton {

                }
            }.show()
        }

        
        btnPrevScreen.setOnClickListener {
            findNavController(R.id.tutorial_nav_host_fragment).navigateUp()
        }


        if (args.showTutorial || SharedPreferencesHelper.get(this).getFirstStart()) {
            findNavController(R.id.tutorial_nav_host_fragment).navigate(R.id.action_global_greetings1Fragment)
        }
    }

    private fun launchMainActivity() {
        startActivity(MainActivity.getIntent(this, R.id.goalOverviewFragment))
        finish()
    }
}
