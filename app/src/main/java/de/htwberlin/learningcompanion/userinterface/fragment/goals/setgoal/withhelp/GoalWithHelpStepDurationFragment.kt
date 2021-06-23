package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.withhelp

import android.app.TimePickerDialog
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.AbsGoalSettingFragment
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.hideKeyboard
import de.htwberlin.learningcompanion.util.setColorForString
import kotlinx.android.synthetic.main.fragment_goal_no_help_user_input.*
import kotlinx.android.synthetic.main.fragment_goal_with_help_step_duration.*
import kotlinx.android.synthetic.main.fragment_goal_with_help_step_duration.btn_done
import kotlinx.android.synthetic.main.fragment_goal_with_help_step_duration.til_for_amount
import java.util.*


class GoalWithHelpStepDurationFragment : AbsGoalSettingFragment() {
    val addUrl : String = "http://192.168.0.27/companion/addGoalnohelp.php"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_with_help_step_duration, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        setClickListener()
        addTimePickerDialogToUntilAmountEditText()


        rb_for_with_help.isEnabled = true
        et_duration_for.setText(goalViewModel.editGoalHolder?.durationInMin)
        et_duration_for.isEnabled = true
//        if (!goalViewModel.editGoalHolder?.durationInMin.isNullOrEmpty()) {
//        } else if (!goalViewModel.editGoalHolder?.untilTimeStamp.isNullOrEmpty()) {
//            rb_until_with_help.isEnabled = true
//            et_duration_until.setText(goalViewModel.editGoalHolder?.untilTimeStamp)
//        }


        setColorForString(tv_goal_with_help_action_info1, ContextCompat.getColor(context!!, R.color.colorAccent), "", SharedPreferencesHelper.get(context!!).getUserName(), getString(R.string.goal_with_help_duration_info1))
    }

    private fun addTimePickerDialogToUntilAmountEditText() {
        et_duration_until.isFocusable = false // this will prevent keyboard from showing
        et_duration_until.clearFocus()

        et_duration_until.setOnClickListener {
            showTimePickerDialogForTextView(it as TextView)
        }
    }

    private fun showTimePickerDialogForTextView(view: TextView) {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
            view.text = createTimeStringFromValues(selectedHour, selectedMinute)
        }, hour, minute, true)
        mTimePicker.setTitle(getString(R.string.select_time))
        mTimePicker.show()
    }

    private fun createTimeStringFromValues(hour: Int, minute: Int): String {
        var minuteString = minute.toString()
        var hourString = hour.toString()

        if (minute < 10) {
            minuteString = "0$minuteString"
        }

        if (hour < 10) {
            hourString = "0$hourString"
        }

        return "$hourString:$minuteString"
    }

    private fun setClickListener() {
        btn_done.setOnClickListener(navigateNext)
        btn_back.setOnClickListener(navigatePrevious)

//        addRadioButtonClickListeners()
    }

    private fun addRadioButtonClickListeners() {
        rb_for_with_help.setOnClickListener {
            et_duration_for.isEnabled = true
            et_duration_until.isEnabled = false
        }

        rb_until_with_help.setOnClickListener {
            et_duration_for.isEnabled = false
            et_duration_until.isEnabled = true
        }
    }

    private val navigatePrevious = fun(_:View) {
        findNavController().popBackStack()
    }

    private val navigateNext = fun(_:View) {
        if (getValues()) {
            addData()
            hideKeyboard(activity!!)
            findNavController().navigate(R.id.action_goalWithHelpStepDurationFragment_to_goalSummaryFragment)
        }
    }

    private fun getValues(): Boolean{

        if (validate(til_for_amount, et_duration_for)) {

            goalViewModel.editGoalHolder?.untilTimeStamp = null
            goalViewModel.editGoalHolder?.durationInMin = et_duration_for.text.toString()

            return true
        } else {
            til_for_amount.isErrorEnabled = true
            return false
        }


//        when (radioGroup.checkedRadioButtonId) {
//            R.id.rb_for_with_help -> {
//
//                goalViewModel.editGoalHolder?.untilTimeStamp = null
//                goalViewModel.editGoalHolder?.durationInMin = et_duration_for.text.toString()
//            }
//            R.id.rb_until_with_help -> {
//
//                goalViewModel.editGoalHolder?.durationInMin = null
//                goalViewModel.editGoalHolder?.untilTimeStamp = et_duration_until.text.toString()
//            }
//        }
    }
    fun addData() {
        val getID = Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID)
        val getTime = Calendar.getInstance().time.toString()
        val getAction = et_action.text.toString()
        val getField = et_field.text.toString()
        val getAmount= et_amount.text.toString()
        val getMedium = et_medium.text.toString()
        val getDuration = et_for_amount.text.toString()

        val postRequest: StringRequest = object : StringRequest(Method.POST, addUrl,
                Response.Listener { response -> // response
                    Log.d("Response", response)
                },
                Response.ErrorListener { response ->// error
                    Log.d("Error.Response", response.toString())
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params.put("id", getID)
                params.put("time", getTime)
                params.put("action", getAction)
                params.put("field", getField)
                params.put("medium", getMedium)
                params.put("amount", getAmount)
                params.put("duration", getDuration)
                return params
            }
        }
        val queue = Volley.newRequestQueue(context!!)
        queue.add(postRequest)
    }
}
