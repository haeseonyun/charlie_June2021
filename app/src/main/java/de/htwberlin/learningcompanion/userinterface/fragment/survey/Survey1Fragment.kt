package de.htwberlin.learningcompanion.userinterface.fragment.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.htwberlin.learningcompanion.R


class Survey1Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_question1, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        activity?.findViewById<TextView>(R.id.btnSkippTutorial)?.visibility = View.INVISIBLE
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.VISIBLE

        activity?.findViewById<TextView>(R.id.btnNextScreen)?.apply {
            text = getString(R.string.next)
            setOnClickListener{
                //Starte Survey_Activity als klasse oder als hier deklarierte funktion
                Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                        Survey1FragmentDirections.actionSurvey1FragmentToSurvey2Fragment()
                )
            }
        }
    }
}
