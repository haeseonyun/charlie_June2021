package de.htwberlin.learningcompanion.userinterface.fragment.start.tutorial


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.activity.MainActivity
import de.htwberlin.learningcompanion.userinterface.fragment.survey.Survey1EvaluateFragmentDirections
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_greetings8.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Greetings8Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greetings8, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        activity?.findViewById<TextView>(R.id.btnSkippTutorial)?.visibility = View.INVISIBLE
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.VISIBLE

        activity?.findViewById<TextView>(R.id.btnNextScreen)?.apply {
            text = getString(R.string.start)

            setOnClickListener {
                SharedPreferencesHelper.get(context!!).setFirstStart(false)
                startActivity(MainActivity.getIntent(context!!, R.id.goalOverviewFragment))
                activity?.finish()
            }
            /*setOnClickListener{
                //Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                    //    Greetings8FragmentDirections.actionGreetings8FragmentToSurveyGraph2())
                findNavController().navigate(
                        Greetings8FragmentDirections.actionGreetings8FragmentToDataSecureFragment()
                )
            }*/
        }

        setColorUsername(context!!, tvTitle, "", getString(R.string.tutorial_greetings8_title))
        setColorUsername(context!!, tvTutorialText2, getString(R.string.tutorial_greetings8_text2), "?")
        imgBuddy.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())
    }
}
