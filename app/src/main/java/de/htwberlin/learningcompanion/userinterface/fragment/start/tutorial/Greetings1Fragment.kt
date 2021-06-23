package de.htwberlin.learningcompanion.userinterface.fragment.start.tutorial


import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController

import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import kotlinx.android.synthetic.main.activity_tutorial.*
import kotlinx.android.synthetic.main.fragment_greetings1.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Greetings1Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greetings1, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.findViewById<TextView>(R.id.btnNextScreen)?.apply {
            visibility = View.VISIBLE

            setOnClickListener {
                if (et_BuddyName.text?.isEmpty()!!) {
                    SharedPreferencesHelper.get(context!!).setBuddyName(getString(R.string.tutorial_default_buddyname))
                } else {
                    SharedPreferencesHelper.get(context!!).setBuddyName(et_BuddyName.text.toString())
                }

                findNavController().navigate(
                        Greetings1FragmentDirections.actionGreetings1FragmentToGreetings2Fragment()
                )
            }
        }
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.GONE

        imgBuddy.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())


        animateFace()
    }

    private fun animateFace() {
        object : CountDownTimer(3000, 100) {
            override fun onFinish() {
                if (imgBuddy != null) imgBuddy.setImageDrawable(BuddyFaceHolder.get(context!!).getThinkingFace())
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }.start()
    }

}
