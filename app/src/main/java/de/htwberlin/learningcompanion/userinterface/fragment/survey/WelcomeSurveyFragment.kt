package de.htwberlin.learningcompanion.userinterface.fragment.survey;

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import de.htwberlin.learningcompanion.R
import kotlinx.android.synthetic.main.fragment_survey_welcome.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class WelcomeSurveyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.VISIBLE
        activity?.findViewById<TextView>(R.id.btnNextScreen)?.apply {
            visibility = View.VISIBLE
           setOnClickListener {
                Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                        WelcomeSurveyFragmentDirections.actionWelcomeSurveyFragmentToSurvey1Fragment4()
                )
            }
        }
    }
}

