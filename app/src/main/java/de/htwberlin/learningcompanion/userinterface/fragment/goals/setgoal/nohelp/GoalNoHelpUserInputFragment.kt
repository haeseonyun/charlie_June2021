package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.nohelp


import android.app.TimePickerDialog
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal.AbsGoalSettingFragment
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.hideKeyboard
import de.htwberlin.learningcompanion.util.setColorForString
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_goal_no_help_user_input.*
import kotlinx.android.synthetic.main.fragment_goal_no_help_user_input.btn_done
import kotlinx.android.synthetic.main.fragment_goal_no_help_user_input.radioGroup
import kotlinx.android.synthetic.main.fragment_survey_evaluate1.*
import java.util.*


class GoalNoHelpUserInputFragment : AbsGoalSettingFragment() {
    /*DATABASE*/
    val addUrl : String = "http://companion.bplaced.net/addGoalnohelp.php"
    //val addUrl : String = "http://companion.hu-informatik.berlin.de/addRunGoal.php"
    private val args: GoalNoHelpUserInputFragmentArgs by navArgs()

    private var editMode = false
    private var goal: Goal? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_goal_no_help_user_input, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setClickListener()

        setColorUsername(context!!, tv_title, "", ", what is your goal?")

        checkForEditableGoal()
    }

    private fun setClickListener() {
        btn_done.setOnClickListener(onDoneButtonClickListener)
        addRadioButtonClickListeners()
        addTimePickerDialogToUntilAmountEditText()
    }

    private fun checkForEditableGoal() {
        if (args.ID != -1L) {
            goal = goalViewModel.repository.getGoalByID(args.ID)

            if (goal != null) {
                initLayoutWithGoal(goal!!)
                editMode = true
            } else {
                editMode = false
            }
        } else {
            editMode = false
        }
    }

    private fun initLayoutWithGoal(goal: Goal) {
        et_action.setText(goal.action)
        et_field.setText(goal.field)
        et_amount.setText(goal.amount)
        et_medium.setText(goal.medium)

        if (goal.durationInMin != null) {
            rb_for.isChecked = true
            et_for_amount.isEnabled = true
            et_until_amount.isEnabled = false
            et_for_amount.setText(goal.durationInMin.toString())
        } else {
            rb_until.isChecked = true
            et_until_amount.isEnabled = true
            et_for_amount.isEnabled = false
            et_until_amount.setText(goal.untilTimeStamp)
        }
    }

    private fun addRadioButtonClickListeners() {
        rb_for.setOnClickListener {
            et_for_amount.isEnabled = true
            et_until_amount.isEnabled = false
        }

        rb_until.setOnClickListener {
            et_for_amount.isEnabled = false
            et_until_amount.isEnabled = true
        }
    }

    private fun addTimePickerDialogToUntilAmountEditText() {
        et_until_amount.isFocusable = false // this will prevent keyboard from showing
        et_until_amount.clearFocus()

        et_until_amount.setOnClickListener {
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

    private val onDoneButtonClickListener = fun(_:View) {
        if (editMode) {
            val actionString = et_action.text.toString()
            val fieldString = et_field.text.toString()
            val amountString = et_amount.text.toString()
            val mediumString = et_medium.text.toString()
            val timestamp = Calendar.getInstance().timeInMillis

            val updateGoal = Goal(
                    id = null,
                    action = actionString,
                    amount = amountString,
                    field = fieldString,
                    medium = mediumString,
                    createdAtTimestamp = timestamp)

//            if (rb_until.isChecked) {
//                et_until_amount.text.toString().let {
//                    if (!it.isEmpty()) {
//                        updateGoal = Goal(null, actionString, amountString, fieldString, mediumString, null, it, timestamp)
//                    }
//                }
//            } else {
//
//            }

            et_for_amount.text.toString().let {
                if (it.isNotEmpty()) {
                    updateGoal.durationInMin = it.toInt()
                }
            }

            if (validateAll()) {
                updateGoal(updateGoal)
                findNavController().navigate(R.id.action_global_mainScreenFragment)
            }
        } else {
            navigateToSummaryFragmentWithValues()
        }
    }

    private fun updateGoal(goal: Goal) {
        goalViewModel.repository.insertGoal(goal) {
            goalViewModel.currentGoal.value = it
        }
    }

    private fun navigateToSummaryFragmentWithValues() {
        if (validateAll()) {
            et_for_amount.text.toString().let {
                if (it.isNotEmpty()) {
                    /*DATABASE*/
                    addData()
                }
            }
            findNavController().navigate(
                    R.id.goalSummaryFragment
            )
        }
    }

    override fun onPause() {
        hideKeyboard(activity!!)
        super.onPause()
    }

    private fun validateAll(): Boolean {
        var isValid = true

        if (validate(til_action, et_action)) {
            goalViewModel.editGoalHolder?.action = et_action.text.toString()

        } else isValid = false

        if (validate(til_field, et_field)) {
            goalViewModel.editGoalHolder?.field = et_field.text.toString()

        } else isValid = false

        if (validate(til_medium, et_medium)) {
            goalViewModel.editGoalHolder?.medium = et_medium.text.toString()

        } else isValid = false

        if (validate(til_amount, et_amount)) {
            goalViewModel.editGoalHolder?.amount = et_amount.text.toString()

        } else isValid = false


        if (validate(til_for_amount, et_for_amount)) {

            goalViewModel.editGoalHolder?.untilTimeStamp = null
            goalViewModel.editGoalHolder?.durationInMin = et_for_amount.text.toString()

        } else isValid = false

//        when (radioGroup.checkedRadioButtonId) {
//            R.id.rb_for -> {
//
//                goalViewModel.editGoalHolder?.untilTimeStamp = null
//                goalViewModel.editGoalHolder?.durationInMin = et_for_amount.text.toString()
//            }
//            R.id.rb_until -> {
//
//                goalViewModel.editGoalHolder?.durationInMin = null
//                goalViewModel.editGoalHolder?.untilTimeStamp = et_until_amount.text.toString()
//            }
//        }

        return isValid
    }
    /*DATABASE*/
    fun addData() {
        val getID = Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID)
        //val getGoalId = goalViewModel.currentGoal.value?.id.toString()
        val getTime = Calendar.getInstance().time.toString()
        val getAction = et_action.text.toString()
        val getField = et_field.text.toString()
        val getMedium = et_medium.text.toString()
        val getAmount = et_amount.text.toString()
        val getDuration = et_for_amount.text.toString()

        //funktioniert nur in der Simulation bei android 7 stürzt es in echt ab
        // val getTime = LocalDateTime.now().toString()

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
             //   params.put("goal_id", getGoalId)
                params.put("time", getTime)
                params.put("action", getAction )
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
