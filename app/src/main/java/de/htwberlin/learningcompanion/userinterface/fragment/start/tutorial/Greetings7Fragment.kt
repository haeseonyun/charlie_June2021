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
import kotlinx.android.synthetic.main.fragment_greetings1.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Greetings7Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greetings7, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        activity?.findViewById<TextView>(R.id.btnSkippTutorial)?.visibility = View.VISIBLE
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.VISIBLE

        activity?.findViewById<TextView>(R.id.btnNextScreen)?.apply {
            text = getString(R.string.next)

            setOnClickListener{
                Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                        Greetings7FragmentDirections.actionGreetings7FragmentToGreetings8Fragment()
                )
            }
        }

        imgBuddy.setImageDrawable(BuddyFaceHolder.get(context!!).getCalmFace())
    }
}
