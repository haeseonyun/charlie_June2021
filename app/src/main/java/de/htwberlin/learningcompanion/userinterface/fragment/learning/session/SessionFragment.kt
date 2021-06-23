package de.htwberlin.learningcompanion.userinterface.fragment.learning.session


import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.formatDuration
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.sensordata.processing.SensorProcessingService
import de.htwberlin.learningcompanion.sensordata.processing.processor.MovementProcessor
import de.htwberlin.learningcompanion.sensordata.processing.processor.OrientationProcessor
import de.htwberlin.learningcompanion.sensordata.source.SensorService
import de.htwberlin.learningcompanion.userinterface.fragment.learning.SessionEvaluator
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.*
import kotlinx.android.synthetic.main.fragment_session.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min


class SessionFragment : Fragment() {

    private val args: SessionFragmentArgs by navArgs()


    private val viewModelFactory = ViewModelFactory
    private lateinit var learningSessionViewModel: LearningSessionViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var goalViewModel: GoalViewModel

    private lateinit var session: LearningSession
    private lateinit var goal: Goal
    private lateinit var place: Place

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)
        learningSessionViewModel = ViewModelProviders.of(this, viewModelFactory).get(LearningSessionViewModel::class.java)

        loadSession()
        initLayoutWithSession()

        btnRecommendations.setOnClickListener(Navigation.createNavigateOnClickListener(
                SessionFragmentDirections.actionGlobalRecommendationFragment()
        ))
    }
    private fun loadSession() {
        val id = args.ID

        session = learningSessionViewModel.repository.getLearningSessionByID(id)
        goal = goalViewModel.repository.getGoalByID(session.goalID)
        place = placeViewModel.repository.getPlaceByID(session.placeID)
    }

    private fun initLayoutWithSession() {
        speedView.speedPercentTo(session.userRating ?: 0, 2000)
        speedView.unit = "% (Your Rating)"

        tv_goal.text = getGoalText(goal)
        tv_place.text = place.name
        tv_address.text = place.addressString
        Glide.with(context!!).load(place.imageUri).fitCenter().into(iv_place_preview)


        if (session.lightValues.isNotEmpty() && session.noiseValues.isNotEmpty()) {
            initBrightnessChart()
            initNoiseChart()
        } else {
            brightness_chart.visibility = View.GONE
            noise_chart.visibility = View.GONE

            tv_noise_value.text = session.noiseRating.name
            tv_brightness_value.text = session.brightnessRating.name
        }


        tvSuccess.text = session.evaluationSuccessFactor
        tvTappingValue.text = session.faceTapped.size.toString()
        tvDistractedValue.text = session.events.count {
            it.value == EVENT_DISTRACTION_ALERT
        }.toString()


        initDistractionChart()
    }

    private fun initBrightnessChart() {
        brightness_chart.setTouchEnabled(false)
        brightness_chart.description = Description().apply { text = "Brightness level" }

        var entries = arrayListOf<Entry>()
        for (i in 0 until session.lightValues.size) {
            entries.add(Entry(i.toFloat(), session.lightValues[i]))
        }

        var dataSet = LineDataSet(entries, "Measured Brightness in Lux")
        dataSet.color = Color.RED
        dataSet.valueTextColor = Color.MAGENTA
        dataSet.setDrawFilled(true)
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        brightness_chart.data = lineData
        brightness_chart.invalidate()

        brightness_chart.axisRight.isEnabled = false
        brightness_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        addLimitLinesToBrightnessChart()
    }

    private fun addLimitLinesToBrightnessChart() {
        val leftAxis = brightness_chart.axisLeft

        var ll = LimitLine(LIGHT_MEDIUM_THRESHOLD.toFloat(), "Medium")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.DKGRAY
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)


        ll = LimitLine(LIGHT_LOW_THRESHOLD.toFloat(), "\u2b07 Dark")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.RED
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)


        ll = LimitLine(LIGHT_HIGH_THRESHOLD.toFloat(), "\u2b06 Bright")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.GREEN
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        var averageBrightness = SessionEvaluator.calculateAverage(session.lightValues)

        ll = LimitLine(averageBrightness.toFloat(), "Your Average")
        ll.lineColor = Color.BLUE
        ll.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        leftAxis.axisMaximum = LIGHT_HIGH_THRESHOLD.plus(20f).toFloat()
        leftAxis.axisMinimum = 0f
    }

    private fun initNoiseChart() {
        noise_chart.setTouchEnabled(false)
        noise_chart.description = Description().apply { text = "Noise level" }

        var entries = arrayListOf<Entry>()
        for (i in 0 until session.noiseValues.size) {
            entries.add(Entry(i.toFloat(), session.noiseValues[i].toFloat()))
        }

        var dataSet = LineDataSet(entries, "Noise Values")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.argb(255, 100, 149, 237)
        dataSet.setDrawFilled(true)
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        noise_chart.data = lineData
        noise_chart.invalidate()

        noise_chart.axisRight.isEnabled = false
        noise_chart.xAxis.position = XAxis.XAxisPosition.BOTTOM


        addLimitLinesToNoiseChart()
    }

    private fun addLimitLinesToNoiseChart() {
        val leftAxis = noise_chart.axisLeft

        var ll = LimitLine(NOISE_MEDIUM_THRESHOLD.toFloat(), "Medium")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.DKGRAY
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        ll = LimitLine(NOISE_LOW_THRESHOLD.toFloat(), "\u2b07 Silent")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.GREEN
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        ll = LimitLine(NOISE_HIGH_THRESHOLD.toFloat(), "\u2b06 Noisy")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.RED
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)


        var averageNoise = SessionEvaluator.calculateAverage(session.noiseValues)

        ll = LimitLine(averageNoise.toFloat(), "Your Average")
        ll.lineColor = Color.BLUE
        ll.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)


        leftAxis.axisMaximum = NOISE_HIGH_THRESHOLD.plus(1000f).toFloat()
        leftAxis.axisMinimum = 0f
    }

    private fun initDistractionChart() {
        chartDistraction.apply {
            setTouchEnabled(false)
            description = Description().apply { text = "Distraction level" }
        }

        var entries = arrayListOf<Entry>()
        for (i in 0 until session.distractionLevel.size) {
            entries.add(Entry(i.toFloat(), session.distractionLevel[i].toFloat()))
        }

        var dataSet = LineDataSet(entries, "Distraction Values").apply {
            color = Color.BLUE
            valueTextColor = Color.argb(255, 100, 149, 237)
            setDrawFilled(true)
            setDrawValues(false)
            setDrawCircles(false)
        }

        val lineData = LineData(dataSet)
        chartDistraction.apply {
            data = lineData
            invalidate()

            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
        }


        addLimitLinesToDistractionChart()
    }

    private fun addLimitLinesToDistractionChart() {
        val leftAxis = chartDistraction.axisLeft

        var ll = LimitLine(DISTRATION_SLIGHTLY_THRESHOLD.toFloat(), "Slightly")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.DKGRAY
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        ll = LimitLine(DISTRATION_MODERATE_THRESHOLD.toFloat(), "\u2b07 Moderate")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.GREEN
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        ll = LimitLine(DISTRATION_HIGH_THRESHOLD.toFloat(), "\u2b06 High")
        ll.enableDashedLine(5f, 10f, 180f)
        ll.lineColor = Color.RED
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        var averageNoise = SessionEvaluator.calculateAverage(session.distractionLevel)

        ll = LimitLine(averageNoise.toFloat(), "Your Average")
        ll.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
        ll.lineColor = Color.BLUE
        ll.lineWidth = 2f
        ll.textColor = Color.BLACK
        ll.textSize = 10f

        leftAxis.addLimitLine(ll)

        leftAxis.axisMaximum = 25f

    }
}
