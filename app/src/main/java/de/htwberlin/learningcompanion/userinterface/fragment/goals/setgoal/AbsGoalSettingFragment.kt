package de.htwberlin.learningcompanion.userinterface.fragment.goals.setgoal

import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputLayout
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory

/**
 * Created by Max on 2019-06-02.
 *
 * @author Max Oehme
 */
open class AbsGoalSettingFragment: Fragment() {

    private val viewModelFactory = ViewModelFactory
    open lateinit var goalViewModel: GoalViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
    }



    open fun validate(textInputLayout: TextInputLayout, editText: EditText): Boolean {
        return editText.text.toString().let {
            if (it.isEmpty() || it.isBlank()) {
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = getString(R.string.error_field_required)

                return@let false
            } else {
                return@let true
            }
        }
    }
}