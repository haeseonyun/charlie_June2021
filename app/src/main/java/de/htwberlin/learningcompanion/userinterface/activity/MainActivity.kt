package de.htwberlin.learningcompanion.userinterface.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.fragment.mainscreen.MainScreenFragmentDirections
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(){
    var mainMenu: Menu? = null
    companion object {
        const val EXTRA_STARTPAGE = "EXTRA_STARTPAGE"
        //globale Variable zum schauen ob dem Datenschutz zugestimmt wurde oder nicht
        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)

        fun getIntent(context: Context, startPage: Int): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_STARTPAGE, startPage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//        supportActionBar?.apply {
//            setDisplayShowTitleEnabled(false)
//        }
        val context: Context = applicationContext
        val navController = findNavController(R.id.content_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.mainScreenFragment,
                        R.id.goalOverviewFragment,
                        R.id.placeOverviewFragment,
                        R.id.sessionOverviewFragment,
                        R.id.recommendationFragment,
                        R.id.settingsOverviewFragment,
                        R.id.helpOverview,
                        R.id.sensorReadOutFragment
                ),
                drawer_layout
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)


        SharedPreferencesHelper.get(applicationContext).buddyColorLiveData.observe(this, Observer {
            setUIElementsToCharlieFace()
        })

        setUIElementsToCharlieFace()


        findNavController(R.id.content_nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            mainMenu?.findItem(R.id.action_buddy)?.isVisible = !(destination.id == R.id.mainScreenFragment ||
                            destination.id == R.id.evaluateGoalAchieved ||
                            destination.id == R.id.evaluatePlaceFragment)

            if (nav_view.menu?.findItem(destination.id) != null) {
                toolbar.navigationIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_menu)
            }

            if (destination.id == R.id.mainScreenFragment) {
                toolbar.title = SharedPreferencesHelper.get(this).getBuddyName()
            }
        }
    }

//    private val sensorObserver = Observer<HashMap<SensorValues, Float>> {
//        Log.d("SENSOR", "${SensorValues.ACCELEROMETER_X}: ${it[SensorValues.ACCELEROMETER_X]}")
//        Log.d("SENSOR", "${SensorValues.ACCELEROMETER_Y}: ${it[SensorValues.ACCELEROMETER_Y]}")
//        Log.d("SENSOR", "${SensorValues.ACCELEROMETER_Z}: ${it[SensorValues.ACCELEROMETER_Z]}")
//        Log.d("SENSOR", "${SensorValues.GYROSCOPE_X}: ${it[SensorValues.GYROSCOPE_X]}")
//        Log.d("SENSOR", "${SensorValues.GYROSCOPE_Y}: ${it[SensorValues.GYROSCOPE_Y]}")
//        Log.d("SENSOR", "${SensorValues.GYROSCOPE_Z}: ${it[SensorValues.GYROSCOPE_Z]}")
//        Log.d("SENSOR", "${SensorValues.LIGHT}: ${it[SensorValues.LIGHT]}")
//    }

    override fun setTitle(titleId: Int) {
        toolbarTitle.text = getString(titleId)
    }

    override fun setTitle(title: CharSequence?) {
        toolbarTitle.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        mainMenu = menu

        mainMenu?.findItem(R.id.action_buddy)?.icon = BuddyFaceHolder.get(this).getDefaultFace()
        mainMenu?.findItem(R.id.action_buddy)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_buddy -> {
                nav_view.setCheckedItem(R.id.mainScreenFragment)
                findNavController(R.id.content_nav_host_fragment).navigate(
                        MainScreenFragmentDirections.actionGlobalMainScreenFragment()
                )
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setDrawerLayoutImageToCharlieFace() {
        runOnUiThread {
            drawer_layout.findViewById<ImageView>(R.id.nav_view_charlie_face)?.setImageDrawable(BuddyFaceHolder.get(applicationContext).getDefaultFace())
        }
    }

    fun setUIElementsToCharlieFace() {
        runOnUiThread {
            drawer_layout.findViewById<ImageView>(R.id.nav_view_charlie_face)?.setImageDrawable(BuddyFaceHolder.get(applicationContext).getDefaultFace())
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }
}
