package de.htwberlin.learningcompanion.userinterface.fragment.learning.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.persistance.model.LearningSession
import de.htwberlin.learningcompanion.userinterface.adapter.GenericRecyclerAdapter
import de.htwberlin.learningcompanion.userinterface.adapter.WidgetData
import de.htwberlin.learningcompanion.userinterface.viewmodel.GoalViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.LearningSessionViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.userinterface.widget.session.SessionListItemData
import de.htwberlin.learningcompanion.userinterface.widget.session.SessionListItemView
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_session_overview.*

class SessionOverviewFragment : Fragment(), AdapterView.OnItemClickListener {

    private val viewModelFactory = ViewModelFactory
    private lateinit var learningSessionViewModel: LearningSessionViewModel
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var goalViewModel: GoalViewModel

    lateinit var genericRecyclerAdapter: GenericRecyclerAdapter<WidgetData>
    var dataList: ArrayList<WidgetData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_session_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        goalViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoalViewModel::class.java)
        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)
        learningSessionViewModel = ViewModelProviders.of(this, viewModelFactory).get(LearningSessionViewModel::class.java)
        learningSessionViewModel.learningSessions.observe(this, learningSessionOverviewObserver)


        genericRecyclerAdapter = GenericRecyclerAdapter(SessionListItemView::class.java)
        genericRecyclerAdapter.onItemClickListener = this
        genericRecyclerAdapter.data = dataList

        rv_sessions.apply {
            layoutManager = LinearLayoutManager(context)

            adapter = genericRecyclerAdapter
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        }


        setBuddyInfoText()
        setBuddyImage()
    }

    private val learningSessionOverviewObserver = Observer<List<LearningSession>> { learningSeaaion ->
        dataList.clear()

        learningSeaaion.forEach {
//            if (it.userRating != null) {
                dataList.add(SessionListItemData(
                        it,
                        goalViewModel.repository.getGoalByID(it.goalID),
                        placeViewModel.repository.getPlaceByID(it.placeID)
                ).apply {
                    id = it.id!!
                })
//            }
        }
        genericRecyclerAdapter.data = dataList

        setBuddyInfoText()
    }

    private fun setBuddyImage() {
        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())
    }

    private fun setBuddyInfoText() {
        if (learningSessionViewModel.learningSessions.value != null &&
                learningSessionViewModel.learningSessions.value!!.isNotEmpty()) {

            setColorUsername(context!!, tv_charlie_info, "", ", this is the overview of your learning history. \nYou can tap each entry to see the details.")
        } else {
            setColorUsername(context!!, tv_charlie_info, "", ", this is the overview of your learning history. \nThere are no sessions to review yet.")
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        findNavController().navigate(
                SessionOverviewFragmentDirections.actionSessionOverviewFragmentToSessionFragment(id)
        )
    }
}
