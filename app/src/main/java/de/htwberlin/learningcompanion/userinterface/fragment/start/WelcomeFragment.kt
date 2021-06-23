package de.htwberlin.learningcompanion.userinterface.fragment.start


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.activity.MainActivity
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import kotlinx.android.synthetic.main.fragment_welcome.*
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.widget.TextView.BufferType
import android.widget.TextView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.graphics.Color
import androidx.core.content.ContextCompat
import de.htwberlin.learningcompanion.util.setColorForString


/**
 * A simple [Fragment] subclass.
 *
 */
class WelcomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        cl_welcome_activity.setOnClickListener {
            launchMainActivity()
        }

        setColorForString(tv_title_intro, ContextCompat.getColor(context!!, R.color.purple), "Hello ",SharedPreferencesHelper.get(context!!).getUserName(), ", I am")

        tv_buddy_name.text = SharedPreferencesHelper.get(context!!).getBuddyName()

        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())
    }

    private fun launchMainActivity() {
        startActivity(Intent(context!!, MainActivity::class.java))
//        startActivity(Intent(this, TutorialActivity::class.java))
        activity?.finish()
    }

}
