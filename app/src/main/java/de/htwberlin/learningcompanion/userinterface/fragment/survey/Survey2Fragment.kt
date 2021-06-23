package de.htwberlin.learningcompanion.userinterface.fragment.survey

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.activity.MainActivity
import de.htwberlin.learningcompanion.userinterface.fragment.start.tutorial.Greetings1FragmentDirections
import de.htwberlin.learningcompanion.userinterface.fragment.start.tutorial.Greetings8FragmentDirections
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_greetings1.*
import kotlinx.android.synthetic.main.fragment_greetings1.imgBuddy
import kotlinx.android.synthetic.main.fragment_greetings1.tvTitle
import kotlinx.android.synthetic.main.fragment_greetings8.*

class Survey2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_question2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        activity?.findViewById<TextView>(R.id.btnSkippTutorial)?.visibility = View.INVISIBLE
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.VISIBLE

        activity?.findViewById<TextView>(R.id.btnNextScreen)?.apply {
            text = getString(R.string.next)

            setOnClickListener{
                Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                        Survey2FragmentDirections.actionSurvey2FragmentToSurvey3Fragment()
                )
            }
        }
    }
}
