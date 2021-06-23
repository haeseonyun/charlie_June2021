package de.htwberlin.learningcompanion.userinterface.widget.session

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.adapter.Bindable
import de.htwberlin.learningcompanion.userinterface.fragment.learning.LightLevel
import de.htwberlin.learningcompanion.userinterface.fragment.learning.NoiseLevel
import kotlinx.android.synthetic.main.session_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Max on 2019-06-01.
 *
 * @author Max Oehme
 */
class SessionListItemView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
): LinearLayout(context, attrs, defStyleAttr, defStyleRes), Bindable<SessionListItemData> {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    val dateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    init {
        View.inflate(context, R.layout.session_list_item, this)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onBind(data: SessionListItemData) {


        val session = data.data

        val goal = data.goal
        val place = data.place

        tv_date.text = dateFormat.format(Date(session.createdAtTimestamp))
        tv_goal.text = getGoalText(goal)
        tv_place.text = place.name
        iv_brightness.setColorFilter(getColorForLightLevel(session.brightnessRating))
        iv_noise.setColorFilter(getColorForNoiseLevel(session.noiseRating))

        iv_rate.setColorFilter(getColorForUserrating(session.userRating ?: 0))

        if (session.userRating ?: 0 >= 70) {
            iv_rate.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up))
        } else {
            iv_rate.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_thumb_down))
        }

    }

    private fun getColorForNoiseLevel(noiseLevel: NoiseLevel): Int {
        return when (noiseLevel) {
            NoiseLevel.LOWEST -> ContextCompat.getColor(context, R.color.lightgreen)
            NoiseLevel.LOW -> ContextCompat.getColor(context, R.color.green)
            NoiseLevel.MEDIUM -> ContextCompat.getColor(context, R.color.orange)
            NoiseLevel.HIGH -> ContextCompat.getColor(context, R.color.lightred)
            NoiseLevel.HIGHEST -> ContextCompat.getColor(context, R.color.red)
        }
    }

    private fun getColorForLightLevel(lightLevel: LightLevel): Int {
        return when (lightLevel) {
            LightLevel.LOWEST -> ContextCompat.getColor(context, R.color.red)
            LightLevel.LOW -> ContextCompat.getColor(context, R.color.lightred)
            LightLevel.MEDIUM -> ContextCompat.getColor(context, R.color.orange)
            LightLevel.HIGH -> ContextCompat.getColor(context, R.color.green)
            LightLevel.HIGHEST -> ContextCompat.getColor(context, R.color.lightgreen)
        }
    }

    private fun getColorForUserrating(userrating: Int): Int {
        return when (userrating) {
            in 0..20 -> ContextCompat.getColor(context, R.color.red)
            in 21..40 -> ContextCompat.getColor(context, R.color.lightred)
            in 41..60 -> ContextCompat.getColor(context, R.color.orange)
            in 61..80 -> ContextCompat.getColor(context, R.color.green)
            in 81..100 -> ContextCompat.getColor(context, R.color.lightgreen)
            else -> ContextCompat.getColor(context, R.color.black)
        }
    }


    fun getGoalText(goal: Goal?): String {
        return goal?.run {
            when {
                untilTimeStamp != null -> "$action $amount $field $medium until $untilTimeStamp"
                durationInMin != null -> "$action $amount $field $medium for $durationInMin minutes"
                else -> "${action}, ${field}, ${medium}, ${amount}"
            }
        }!!
    }
}