package de.htwberlin.learningcompanion.userinterface.fragment.mainscreen


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.htwberlin.learningcompanion.COMM_DISTRACTION_EVENT
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.Buddy
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.userinterface.fragment.learning.DistractionLevel
import de.htwberlin.learningcompanion.userinterface.fragment.learning.SessionEvaluator
import de.htwberlin.learningcompanion.userinterface.service.LearningSeassionService
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.*
import kotlinx.android.synthetic.main.fragment_main_screen.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*
import java.util.concurrent.TimeUnit

class MainScreenFragment : Fragment() {


    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        const val REQUEST_EXTERNAL_STORAGE_PERMISSION = 201
        const val REQUEST_ALL_PERMISSION = 202
    }

    private val viewModelFactory = ViewModelFactory
    private lateinit var learningSessionViewModel: LearningSessionViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var goalViewModel: GoalViewModel

    private var permissionToRecordAccepted = false
    private var waitingForPermissionToStartSession = false

    private lateinit var buddy: Buddy
    private var distrationLevel = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hideKeyboard(activity!!)

        learningSessionViewModel = ViewModelProviders.of(this, viewModelFactory).get(LearningSessionViewModel::class.java)
        learningSessionViewModel.currentLearningSession.observe(this, learningSessionObserver)
        goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
        goalViewModel.currentGoal.observe(this, currentGoalObserver)
        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)
        placeViewModel.currentPlace.observe(this, currentPlaceObserver)

        buddy = Buddy.get(context!!)

        addClickListeners()
        setBackgroundPicture()
        showCharlieInfoText()

        observeBuddyLiveData()

        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())
        tv_charlie_text.visibility = View.VISIBLE
        setColorUsername(context!!, tv_charlie_text, "", buddy.getInfoText())
        cl_charlie_info.visibility = View.GONE
        setupStartButton()

        activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerOpened(drawerView: View) {
                if (learningSessionViewModel.isSessionRunning()) {
                    activity?.findViewById<DrawerLayout>(R.id.drawer_layout)?.closeDrawer(drawerView)

                    buddy.showExitProhibitedMessage()
                }
            }

        })

        activity?.registerReceiver(receiver, IntentFilter().apply {
            addAction(COMM_DISTRACTION_EVENT)
        })
    }

    override fun onDestroy() {
        activity?.unregisterReceiver(receiver)
        super.onDestroy()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.hasExtra(EVENT_DISTRACTION_ALERT) == true) {
                if (p1.getBooleanExtra(EVENT_DISTRACTION_ALERT, false)) {
                    activity?.runOnUiThread {
                        buddy.showKeepLearningText()
                    }
                }
            }
        }
    }

    private val currentGoalObserver = Observer<Goal?> {
        buddy.currentGoal = it

        tv_charlie_info.text = buddy.getInfoText()
        buddy.setInfo()
        setupStartButton()
    }

    private val currentPlaceObserver = Observer<Place?> {
        buddy.currentPlace = it

        tv_charlie_info.text = buddy.getInfoText()
        buddy.setInfo()
        setupStartButton()
    }

    private fun observeBuddyLiveData() {
        SharedPreferencesHelper.get(context!!).buddyColorLiveData.observe(this, Observer {
            buddy.drawableLiveData.value = BuddyFaceHolder.get(context!!).getDefaultFace()
        })
        buddy.drawableLiveData.observe(this, Observer<Drawable> { drawable -> iv_charlie.setImageDrawable(drawable) })
        buddy.speechLiveData.observe(this, Observer<String> { text ->
            run {
                if (text.isEmpty() || learningSessionViewModel.isSessionRunning().not()) {
                    if (learningSessionViewModel.isSessionRunning()) {
                        tv_charlie_text.visibility = View.INVISIBLE
                        tv_charlie_text.text = ""
                    } else {
                        tv_charlie_text.visibility = View.VISIBLE
                        setColorUsername(context!!, tv_charlie_text, "", buddy.getInfoText(formatted = true))
                    }
                } else {
                    tv_charlie_text.visibility = View.VISIBLE
                    tv_charlie_text.text = text
                }
            }
        })

        iv_charlie.setOnClickListener {
            if (buddy.isInDefaultState) {
                if (learningSessionViewModel.isSessionRunning()) {

                    var showWarning = false
                    learningSessionViewModel.currentLearningSession.value?.let { session ->
                        val lastMap = session.faceTapped
                        if (lastMap.size >= 3) {
                            if ((learningSessionViewModel.goalProgressTime.value ?: 0) - lastMap[lastMap.size - 3] < 35000) {
                                showWarning = true
                            }
                        }
                    }

                    if (showWarning) {
                        buddy.showKeepLearningText()
                    } else {
                        buddy.setNewRandomBuddyLearningText()

                        learningSessionViewModel.goalProgressTime.value?.let {
                            learningSessionViewModel.repository.updateTappingEventInLatestSession(it)
                        }
                    }

                } else {
                    buddy.setNewRandomBuddyBeforeLearningText()
                }
            }

        }
    }

    private fun setBackgroundPicture() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val currentPlace = placeViewModel.currentPlace.value

            if (currentPlace != null) {
                if (currentPlace.imageUri != null) {
                    Glide.with(context!!).load(currentPlace.imageUri).fitCenter().into(iv_place_background)
                }
            }
        } else {
            requestAllPermissions()
        }
    }

    private fun addClickListeners() {
        btn_quit.setOnClickListener {
            openQuitDialog()
        }
    }

    private fun openQuitDialog() {
        alert("We can finish this goal together!\n" +
                "Do you REALLY want to quit?", "Quit learning session") {
            yesButton {
                activity?.stopService(Intent(context, LearningSeassionService::class.java))
                finishLearningSession()
            }
            noButton {
                toast("Good :)")
            }
        }.show()
    }

    private fun openConfirmStartDialog() {
        val currentGoal = goalViewModel.currentGoal.value
        val currentPlace = placeViewModel.currentPlace.value
        alert("Your goal is ${getGoalText(currentGoal)} and you are at ${currentPlace?.name}?", "Confirm Start") {
            yesButton {
                startLearningSession()
            }
            noButton {
                toast("Change it as you like before starting")
            }
        }.show()
    }

    private fun finishLearningSession() {
        cl_charlie_info.visibility = View.GONE

//        activity?.stopService(Intent(context, LearningSeassionService::class.java))
        learningSessionViewModel.endSession()


        findNavController().navigate(
                MainScreenFragmentDirections.actionMainScreenFragmentToEvaluateGraph()
        )

        btn_start.visibility = View.VISIBLE
        btn_quit.visibility = View.INVISIBLE
    }

    private fun onStartButtonClick() {
        if (canStartSession()) {

            waitingForPermissionToStartSession = true
            requestAudioPermission()

            // this will be set immediately if permission is already granted
            if (permissionToRecordAccepted) {
                openConfirmStartDialog()
            }
        } else {
            buddy.setInstructionText()
        }
    }

    /**
     * Session Starts
     */
    private fun startLearningSession() {
        ContextCompat.startForegroundService(context!!, Intent(context, LearningSeassionService::class.java))

        btn_start.visibility = View.INVISIBLE
        btn_quit.visibility = View.VISIBLE

        val goalID = goalViewModel.currentGoal.value!!.id
        val placeID = placeViewModel.currentPlace.value!!.id

        val session = LearningSession(
                id = null,
                placeID = placeID!!,
                goalID = goalID!!,
                createdAtTimestamp = Calendar.getInstance().timeInMillis
//                    studyDuration = (goalViewModel.currentGoal.value!!.durationInMin!! * 60 * 1000) - sessionHandler.remainingMillis
        )

        learningSessionViewModel.repository.insertLearningSession(session) {
            learningSessionViewModel.currentLearningSession.value = it
            learningSessionViewModel.startSession()
        }


        learningSessionViewModel.goalProgressTime.value = 0L
        learningSessionViewModel.goalProgressTime.observe(this, learningSessionTimeObserver)

//        if (permissionToRecordAccepted) {
//            sessionHandler.startLearningSessionWithMeasuringSensors()
//        } else {
//            sessionHandler.startLearningSessionWithoutMeasuringSensors()
//        }

        buddy.showStartLearningText()
    }

    private val learningSessionObserver = Observer<LearningSession> { session ->
        val evaluator = SessionEvaluator(session?.lightValues ?: emptyList(), session?.noiseValues ?: emptyList())
        val lightLevel = evaluator.evaluateLight()
        val noiseLevel = evaluator.evaluateNoise()

        distrationLevel = session.distractionLevel.last()
        val distractionLevel = DistractionLevel.fromValue(distrationLevel.toDouble())

        tv_learning_info.text =  "Light: ${lightLevel.levelName} \nNoise: ${noiseLevel.levelName} \nDistration: ${distractionLevel.levelName}"
    }

    private val learningSessionTimeObserver = Observer<Long?> { progress ->
        if (progress != null) {
            cl_charlie_info.visibility = View.VISIBLE
            setRemainingTIme((learningSessionViewModel.currentLearningGoalEndTime ?: 0) - progress)
        } else {
            tv_learning_info.text = "Learning session over"
            cl_charlie_info.visibility = View.GONE
            finishLearningSession()
        }
    }

    private fun setRemainingTIme(time: Long) {
        val timeString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)))

        tv_learning_info_time.text = "Remaining time: $timeString"
    }


    private fun canStartSession(): Boolean {
        val currentGoal = goalViewModel.currentGoal.value
        val currentPlace = placeViewModel.currentPlace.value

        return when {
            currentGoal == null -> false
            currentPlace == null -> false
            else -> true
        }
    }

    private fun showCharlieInfoText() {
        tv_charlie_info.text = buddy.getInfoText()
    }

    private fun requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        } else {
            permissionToRecordAccepted = true
        }
    }

    private fun requestAllPermissions() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permissions, REQUEST_ALL_PERMISSION)
        } else {
            permissionToRecordAccepted = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

            if (waitingForPermissionToStartSession) {
                startLearningSession()
            }
        } else if (requestCode == REQUEST_ALL_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

            if (waitingForPermissionToStartSession) {
                openConfirmStartDialog()
            }
        }
    }

    private fun setupStartButton() {
        when {
            buddy.currentGoal == null -> {
                btn_start.text = getString(R.string.mainscreen_gotogoal)
                btn_start.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_global_goalOverviewFragment))
            }
            buddy.currentPlace == null -> {
                btn_start.text = getString(R.string.mainscreen_gotoplace)
                btn_start.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_global_placeOverviewFragment))
            }
            else -> {
                btn_start.text = getString(R.string.mainscreen_start)
                btn_start.setOnClickListener {
                    onStartButtonClick()
                }
            }
        }
    }
}
