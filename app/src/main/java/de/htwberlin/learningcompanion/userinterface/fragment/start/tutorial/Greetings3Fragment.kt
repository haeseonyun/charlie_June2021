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
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_greetings3.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Greetings3Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greetings3, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.findViewById<TextView>(R.id.btnNextScreen)?.setOnClickListener{
            if (et_UserName.text?.isEmpty()!!) {
                SharedPreferencesHelper.get(context!!).setUserName(getString(R.string.tutorial_default_username))
            } else {
                SharedPreferencesHelper.get(context!!).setUserName(et_UserName.text.toString())
            }

            Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                    Greetings3FragmentDirections.actionGreetings3FragmentToGreetings6Fragment()
            )
        }

        tvTitleBuddy.text = SharedPreferencesHelper.get(context!!).getBuddyName()
        imgBuddy.setImageDrawable(BuddyFaceHolder.get(context!!).getGrinningFace())
    }
}
