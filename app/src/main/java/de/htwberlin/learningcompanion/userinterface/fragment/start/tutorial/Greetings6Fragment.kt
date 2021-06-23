package de.htwberlin.learningcompanion.userinterface.fragment.start.tutorial


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation

import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.setColorForString
import kotlinx.android.synthetic.main.fragment_greetings6.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Greetings6Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greetings6, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.findViewById<TextView>(R.id.btnNextScreen)?.setOnClickListener{
            Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                    Greetings6FragmentDirections.actionGreetings6FragmentToGreetings7Fragment()
            )
        }

        setColorForString(tvTitle, ContextCompat.getColor(context!!, R.color.purple), "Nice to meet you  ",SharedPreferencesHelper.get(context!!).getUserName(), "!")

//        tvTitle.text = getString(R.string.tutorial_greetings6_title, SharedPreferencesHelper.get(context!!).getUserName())
        imgBuddy.setImageDrawable(BuddyFaceHolder.get(context!!).getGrinningFace())
    }
}
