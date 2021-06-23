package de.htwberlin.learningcompanion.userinterface.widget.placeoverview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.persistance.model.Goal
import de.htwberlin.learningcompanion.userinterface.adapter.Bindable
import de.htwberlin.learningcompanion.userinterface.fragment.places.overview.PlaceOverviewFragment.Companion.TAG_EDIT
import kotlinx.android.synthetic.main.goal_list_item.view.*
import kotlinx.android.synthetic.main.place_list_item.view.*

/**
 * Created by Max on 2019-06-01.
 *
 * @author Max Oehme
 */
class PlaceOverviewItemView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
): LinearLayout(context, attrs, defStyleAttr, defStyleRes), Bindable<PlaceOverviewItemData> {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        View.inflate(context, R.layout.place_list_item, this)

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onBind(data: PlaceOverviewItemData) {
        btn_edit.setOnClickListener {
            tag = TAG_EDIT
            performClick()
        }

        data.data.let { place ->
            tv_name.text = place.name
            tv_address.text = place.addressString

            cb_current_place.isChecked = data.isCurrentPlace
            cb_current_place.setOnClickListener {
                performClick()
            }

            Glide.with(rootView.context).load(place.imageUri).fitCenter().into(iv_place_preview)

        }

    }
}