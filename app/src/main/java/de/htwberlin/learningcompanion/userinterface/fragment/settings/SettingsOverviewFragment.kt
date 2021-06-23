package de.htwberlin.learningcompanion.userinterface.fragment.settings

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import de.htwberlin.learningcompanion.userinterface.activity.MainActivity
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.hideKeyboard
import de.htwberlin.learningcompanion.util.setActivityTitle
import kotlinx.android.synthetic.main.fragment_settings_overview.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast

class SettingsOverviewFragment : Fragment() {

    private var charlieNumberChange = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setSwitchStands()
        addClickListeners()

        setHints()
        setNoticeTextWithCharlie()
    }

    private fun setNoticeTextWithCharlie() {
        val charlieName = SharedPreferencesHelper.get(context!!).getBuddyName()

        tv_settings_intervention_notice.text = charlieName + " " + getString(R.string.settings_intervation_frequency_notice)
        tv_settings_frequency_notice.text = charlieName + " " + getString(R.string.settings_frequency_remark)
        tv_settings_interval_notice.text = charlieName + " " + getString(R.string.settings_interval_remark)
    }

    private fun setHints() {
        input_user_name.hint = SharedPreferencesHelper.get(context!!).getUserName()
        input_buddy_name.hint = SharedPreferencesHelper.get(context!!).getBuddyName()

        tv_settings_buddy_image_text.text = SharedPreferencesHelper.get(context!!).getBuddyMood() + " " + SharedPreferencesHelper.get(context!!).getBuddyName()

        et_settings_interval.hint = SharedPreferencesHelper.get(context!!).getInterval().toString() + " minute(s)"
        et_settings_frequency.hint = SharedPreferencesHelper.get(context!!).getFrequency().toString() + " time(s)"
    }

    private fun savePreferences() {
        var userName = SharedPreferencesHelper.get(context!!).getUserName()
        if (input_user_name.text.none().not()) {
            userName = input_user_name.text.toString()
        }

        var buddyName = SharedPreferencesHelper.get(context!!).getBuddyName()
        if (input_buddy_name.text.none().not()) {
            buddyName = input_buddy_name.text.toString()
        }

        var interval = SharedPreferencesHelper.get(context!!).getInterval()
        if (et_settings_interval.text?.none()?.not() == true) {
            interval = et_settings_interval.text.toString().toInt()
        }

        var frequency = SharedPreferencesHelper.get(context!!).getFrequency()
        if(et_settings_frequency.text?.none()?.not() == true) {
            frequency = et_settings_frequency.text.toString().toInt()
        }

        saveUserName(userName)
        saveBuddyName(buddyName)
        saveBuddyMood(charlieNumberChange)

        saveInterval(interval)
        saveFrequency(frequency)

        setHints()

        sw_gps.setText("")
        input_buddy_name.setText("")
        et_settings_interval.setText("")
        et_settings_frequency.setText("")

        (activity as MainActivity).mainMenu?.findItem(R.id.action_buddy)?.icon = BuddyFaceHolder.get(context!!).getDefaultFace()
    }

    private fun saveUserName(userName: String) {
        SharedPreferencesHelper.get(context!!).setUserName(userName)
    }

    private fun saveBuddyName(buddyName: String) {
        SharedPreferencesHelper.get(context!!).setBuddyName(buddyName)
    }

    private fun saveBuddyMood(moodNumber: Int) {
        SharedPreferencesHelper.get(context!!).setBuddyMood(moodNumber)
    }

    private fun saveInterval(interval: Int) {
        SharedPreferencesHelper.get(context!!).setInterval(interval)
    }

    private fun saveFrequency(frequency: Int) {
        SharedPreferencesHelper.get(context!!).setFrequency(frequency)
    }

    private fun changeMoodText(newName: String) {
        tv_settings_buddy_image_text.text = newName + " " + SharedPreferencesHelper.get(context!!).getBuddyName()
    }

    private fun addClickListeners() {
        iv_charlie_1.setOnClickListener {
            changeMoodText("gentle")
            charlieNumberChange = 1
        }
        iv_charlie_2.setOnClickListener {
            changeMoodText("nerdy")
            charlieNumberChange = 2
        }
        iv_charlie_3.setOnClickListener {
            changeMoodText("calm")
            charlieNumberChange = 3
        }
        iv_charlie_4.setOnClickListener {
            changeMoodText("happy")
            charlieNumberChange = 4
        }
        iv_charlie_5.setOnClickListener {
            changeMoodText("goofy")
            charlieNumberChange = 5
        }
        btn_settings_save.setOnClickListener {
            savePreferences()
            toast("settings were saved")
            hideKeyboard(activity!!)
            findNavController().navigate(R.id.action_global_mainScreenFragment)
        }
    }

    private fun setSwitchStands() {
        sw_microphone.isChecked = hasAudioPermission()
        sw_gps.isChecked = hasLocationPermission()
        sw_camera.isChecked = hasWritePermission()   // ist es hier, oder ein anderes Befehl?
    }

    // check permissions with
    private fun hasWritePermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun tintTextInputLayout(layout: TextInputLayout, errorTint: Boolean) {
        runOnUiThread {
            if (errorTint)
                layout.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.holo_red_dark))
            else
                layout.defaultHintTextColor = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
        }
    }
}