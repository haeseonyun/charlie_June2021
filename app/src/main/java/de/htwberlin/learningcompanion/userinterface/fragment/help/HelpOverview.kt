package de.htwberlin.learningcompanion.userinterface.fragment.help

import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.util.SharedPreferencesHelper
import de.htwberlin.learningcompanion.util.setActivityTitle

class HelpOverview : Fragment() {
    private lateinit var rootView : View

    private lateinit var tvHelpText1: TextView
    private lateinit var tvHelpText2: TextView
    private lateinit var tvHelpText3: TextView
    private lateinit var tvHelpText4: TextView
    private lateinit var tvHelpText5: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_help, container, false)
        findViews()

        setHelpText()

        return rootView
    }

    private fun setHelpText() {

        val charlieName = SharedPreferencesHelper.get(context!!).getBuddyName()
        val text1 = " is designed to help you to set, monitor and review your learning goal and provide you with information on learning places with additional data such as brightness, noise level."
        val text2 = "Messages that "+ charlieName + " provide are based on volitional control strategies in addition to jokes and quotes. The source of the jokes and quotes are as follows: \nhttps://www.brainyquote.com \nhttps://www.lifehack.org \nhttps://www.rd.com/jokes/funny-quotes/ \nhttp://pun.me/pages/funny-quotes.php \nhttp://coolfunnyquotes.com \nhttp://www.laughfactory.com"
        val text3 = "When evaluating your set goal, " + charlieName + " will store data so that " + charlieName + " can recommend you the best place, best time or even best goal that you can achieve."
        val text4 = "You can personalize " + charlieName + " so that " + charlieName + " can call you by name and you can also call " + charlieName + " in your personal way."
        val text5 = "Colors scheme of " + charlieName + " are based on affective colors of trustworthy and playful to display positive affective state."

        val spannableString1 = SpannableString(charlieName + text1)
        spannableString1.setSpan(StyleSpan(Typeface.BOLD), 0, charlieName.length, 0)
        tvHelpText1.text = spannableString1

        val spannableString2 = SpannableString(text2)
        spannableString2.setSpan(StyleSpan(Typeface.BOLD), 0, "Messages".length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString2.setSpan(StyleSpan(Typeface.BOLD), "Messages that ".length, "Messages that ".length + charlieName.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tvHelpText2.text = spannableString2

        val spannableString3 = SpannableString(text3)
        spannableString3.setSpan(StyleSpan(Typeface.BOLD), "When ".length, "when ".length + "evaluating".length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString3.setSpan(
                StyleSpan(Typeface.BOLD),
                "When evaluating your set goal,".length,
                "When evaluating your set goal, ".length + charlieName.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString3.setSpan(
                StyleSpan(Typeface.BOLD),
                "When evaluating your set goal, ".length + charlieName.length + " will store data so that ".length,
                "When evaluating your set goal, ".length + charlieName.length + " will store data so that ".length + charlieName.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        tvHelpText3.text = spannableString3

        val spannableString4 = SpannableString(text4)
        spannableString4.setSpan(
                StyleSpan(Typeface.BOLD),
                "You can personalize ".length,
                "You can personalize ".length + charlieName.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString4.setSpan(
                StyleSpan(Typeface.BOLD),
                ("You can personalize " + charlieName + " so that ").length,
                ("You can personalize " + charlieName + " so that ").length + charlieName.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString4.setSpan(
                StyleSpan(Typeface.BOLD),
                ("You can personalize " + charlieName + " so that " + charlieName + " can call you by name and you can also call ").length,
                ("You can personalize " + charlieName + " so that " + charlieName + " can call you by name and you can also call ").length + charlieName.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        tvHelpText4.text = spannableString4

        val spannableString5 = SpannableString(text5)
        spannableString5.setSpan(StyleSpan(Typeface.BOLD), 0, "Colors".length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString5.setSpan(
                StyleSpan(Typeface.BOLD),
                "Colors scheme of ".length,
                "Colors scheme of ".length + charlieName.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tvHelpText5.text = spannableString5

    }


    private fun findViews() {
        tvHelpText1 = rootView.findViewById(R.id.tv_help_1)
        tvHelpText2 = rootView.findViewById(R.id.tv_help_2)
        tvHelpText3 = rootView.findViewById(R.id.tv_help_3)
        tvHelpText4 = rootView.findViewById(R.id.tv_help_4)
        tvHelpText5 = rootView.findViewById(R.id.tv_help_5)
    }
}