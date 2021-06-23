package de.htwberlin.learningcompanion.userinterface.fragment.places.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.buddy.BuddyFaceHolder
import de.htwberlin.learningcompanion.persistance.model.Place
import de.htwberlin.learningcompanion.userinterface.adapter.GenericRecyclerAdapter
import de.htwberlin.learningcompanion.userinterface.adapter.WidgetData
import de.htwberlin.learningcompanion.userinterface.fragment.places.details.MyPlaceFragment
import de.htwberlin.learningcompanion.userinterface.viewmodel.PlaceViewModel
import de.htwberlin.learningcompanion.userinterface.viewmodel.ViewModelFactory
import de.htwberlin.learningcompanion.userinterface.widget.placeoverview.PlaceOverviewItemData
import de.htwberlin.learningcompanion.userinterface.widget.placeoverview.PlaceOverviewItemView
import de.htwberlin.learningcompanion.util.setActivityTitle
import de.htwberlin.learningcompanion.util.setColorUsername
import kotlinx.android.synthetic.main.fragment_place_overview.*


class PlaceOverviewFragment : Fragment(), AdapterView.OnItemClickListener  {

    companion object {
        const val TAG_EDIT = 1
    }

    private val viewModelFactory = ViewModelFactory
    private lateinit var placeViewModel: PlaceViewModel


    private lateinit var genericRecyclerAdapter: GenericRecyclerAdapter<WidgetData>
    private val placeList = ArrayList<PlaceOverviewItemData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setActivityTitle(getString(R.string.title_nav_menu_place))

        placeViewModel = ViewModelProviders.of(this, viewModelFactory).get(PlaceViewModel::class.java)
        placeViewModel.currentPlace.observe(this, currentPlaceObserver)
        placeViewModel.places.observe(this, placesOverviewObserver )
        updateHeaderLayout()

        genericRecyclerAdapter = GenericRecyclerAdapter(PlaceOverviewItemView::class.java)
        genericRecyclerAdapter.onItemClickListener = this
        genericRecyclerAdapter.data = placeList

        rv_places.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = genericRecyclerAdapter

        }

        btn_new_place.setOnClickListener(Navigation.createNavigateOnClickListener(
                PlaceOverviewFragmentDirections.actionPlaceOverviewFragmentToMyPlaceFragment()
        ))

        btn_edit_place.setOnClickListener(navigateToEditPlace)
    }

    private val placesOverviewObserver = Observer<List<Place>> { places ->
        placeList.clear()

        places.forEach {
            placeList.add(PlaceOverviewItemData(it, placeViewModel.currentPlace.value == it))
        }

        genericRecyclerAdapter.data = placeList
        updateHeaderLayout()
    }

    private val currentPlaceObserver = Observer<Place?> {currentPlace ->
        if (currentPlace != null) {
            placeList.forEach {
                it.isCurrentPlace = it.data == currentPlace
            }
            genericRecyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun updateHeaderLayout() {
        iv_charlie.setImageDrawable(BuddyFaceHolder.get(context!!).getDefaultFace())

        if (placeViewModel.places.value?.isEmpty() == true) {

            setColorUsername(context!!, tv_charlie_info, "", ", you don't have added any places yet. \nYou can add new places by clicking on the \"Add\"-Button.")
        } else {
            if (placeViewModel.currentPlace.value != null) {
                setColorUsername(context!!, tv_charlie_info, "", ", your currently selected place is: ${placeViewModel.currentPlace.value?.name}")
            } else {
                setColorUsername(context!!, tv_charlie_info, "", ", you do not have a place selected. \nGo ahead and add a new place or choose an existing one.")
            }
        }
    }

    private val navigateToEditPlace = fun(_:View){
        placeViewModel.currentPlace.value?.let {
            findNavController().navigate(
                    PlaceOverviewFragmentDirections.actionPlaceOverviewFragmentToMyPlaceFragment(it.id!!)
            )
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (view?.tag == TAG_EDIT) {
            findNavController().navigate(
                    PlaceOverviewFragmentDirections.actionPlaceOverviewFragmentToMyPlaceFragment(placeList[position].data.id!!)
            )
        } else {
            placeViewModel.currentPlace.value = placeList[position].data
        }

        updateHeaderLayout()

    }
}
