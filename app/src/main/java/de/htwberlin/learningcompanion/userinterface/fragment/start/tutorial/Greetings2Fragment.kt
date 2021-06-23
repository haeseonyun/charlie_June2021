package de.htwberlin.learningcompanion.userinterface.fragment.start.tutorial


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_greetings2.*
import kotlinx.android.synthetic.main.fragment_greetings2.tvTitle
import kotlinx.android.synthetic.main.fragment_greetings2.tvTitle2


/**
 * A simple [Fragment] subclass.
 *
 */
class Greetings2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greetings2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.findViewById<TextView>(R.id.btnSkippTutorial)?.visibility = View.VISIBLE
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.VISIBLE
        activity?.findViewById<TextView>(R.id.btnNextScreen)?.apply {
            text = getString(R.string.next)

            setOnClickListener{
                Navigation.findNavController(activity!!, R.id.tutorial_nav_host_fragment).navigate(
                        Greetings2FragmentDirections.actionGreetings2FragmentToGreetings3Fragment()
                )
            }
        }

        tvTitle.text = getString(R.string.tutorial_greetings2_title, SharedPreferencesHelper.get(context!!).getBuddyName())
        tvTitle2.text = getString(R.string.tutorial_greetings2_title2, faceString(SharedPreferencesHelper.get(context!!).getCharlieNum()), SharedPreferencesHelper.get(context!!).getBuddyName())


        setClickListeners()
    }


    private fun setClickListeners() {
        imgBuddy1.setOnClickListener(onClickBuddy)
        imgBuddy2.setOnClickListener(onClickBuddy)
        imgBuddy3.setOnClickListener(onClickBuddy)
        imgBuddy4.setOnClickListener(onClickBuddy)
        imgBuddy5.setOnClickListener(onClickBuddy)
    }

    private val onClickBuddy = fun(view:View) {
        val face: Int = when (view.id) {
            R.id.imgBuddy1 -> 4
            R.id.imgBuddy2 -> 1
            R.id.imgBuddy3 -> 2
            R.id.imgBuddy4 -> 3
            R.id.imgBuddy5 -> 5
            else -> 1
        }


        tvTitle2.text = getString(R.string.tutorial_greetings2_title2, faceString(face), SharedPreferencesHelper.get(context!!).getBuddyName())
        SharedPreferencesHelper.get(context!!).setBuddyMood(face)
    }

    private fun faceString(face: Int): String {
        return when(face) {
            1 -> "gentle"
            2 -> "nerdy"
            3 -> "calm"
            4 -> "happy"
            5 -> "goofy"
            else -> "gentle"
        }
    }


}
