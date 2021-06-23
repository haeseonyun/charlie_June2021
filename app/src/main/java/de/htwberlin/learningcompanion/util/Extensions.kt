package de.htwberlin.learningcompanion.util

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.activity.MainActivity
import kotlinx.android.synthetic.main.app_bar_main.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemService
import de.htwberlin.learningcompanion.R
import kotlinx.android.synthetic.main.fragment_welcome.*


fun Fragment.setActivityTitle(title: String) {
//    (activity as MainActivity).supportActionBar?.title = title
//    (activity as MainActivity).title = title
    activity?.toolbar?.title = title
}

fun getGoalText(goal: Goal?): String? {
    return goal?.run {
        when {
            untilTimeStamp != null -> "$action $amount $field $medium until $untilTimeStamp"
            durationInMin != null -> "$action $amount $field $medium for $durationInMin minutes"
            else -> "${action}, ${field}, ${medium}, ${amount}"
        }
    }
}

fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


fun setColorForString(textView: TextView, color: Int, start: String, center: String, end: String) {
    val builder = SpannableStringBuilder()

    val str1 = SpannableString(start)
    builder.append(str1)

    val str2 = SpannableString(center)
    str2.setSpan(ForegroundColorSpan(color), 0, str2.length, 0)
    builder.append(str2)

    val str3 = SpannableString(end)
    builder.append(str3)

    textView.setText(builder, TextView.BufferType.SPANNABLE)
}

fun setColorUsername(context: Context, textView: TextView, start: String, end: String) {

    setColorForString(textView, ContextCompat.getColor(context, R.color.colorAccent), start, SharedPreferencesHelper.get(context).getUserName(), end)
}