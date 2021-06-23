package de.htwberlin.learningcompanion.userinterface.fragment.learning.evaluation


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.util.hideKeyboard
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_evaluate_success_factor.*

class EvaluateSuccessFactor : Fragment() {


    private val viewModelFactory = ViewModelFactory
    private lateinit var learningSessionViewModel: LearningSessionViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var goalViewModel: GoalViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_evaluate_success_factor, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)
        learningSessionViewModel = ViewModelProviders.of(this, viewModelFactory).get(LearningSessionViewModel::class.java)

        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())

        setBackgroundPicture()
        addButtonClickListener()

//        tvSuccessFactor.text = if (learningSessionViewModel.currentLearningSession.value?.userRating < 50) {
//            ""
//        } else {
//            ""
//        }
        
        setColorUsername(context!!, tvSuccessFactor, "", getString(R.string.evaluation_question_successfactor))
    }

    private fun addButtonClickListener() {
        btn_next.setOnClickListener {
            updateSuccessFactor()

            hideKeyboard(activity!!)

            findNavController().navigate(EvaluateSuccessFactorDirections.actionEvaluateSuccessFactorToEvaluatePlaceFragment())
        }

        btn_skip.setOnClickListener {
            edittextSuccessFactor.text?.clear()

            hideKeyboard(activity!!)

            findNavController().navigate(EvaluateSuccessFactorDirections.actionEvaluateSuccessFactorToEvaluatePlaceFragment())
        }
    }

    private fun updateSuccessFactor() {
        if (edittextSuccessFactor.text.isNullOrBlank().not()) {
            learningSessionViewModel.currentLearningSession.value?.let { currentLearningSession ->
                currentLearningSession.evaluationSuccessFactor = edittextSuccessFactor.text.toString()

                learningSessionViewModel.repository.updateLearningSession(currentLearningSession)
            }
        }
    }

    private fun setBackgroundPicture() {
        placeViewModel.currentPlace.value?.let { currentPlace ->
            if (currentPlace.imageUri != null) {
                val inputStream = activity!!.contentResolver.openInputStream(currentPlace.imageUri!!)
                val drawable = Drawable.createFromStream(inputStream, currentPlace.imageUri.toString())
                iv_place_background.setImageDrawable(drawable)
            }
        }
    }
}
