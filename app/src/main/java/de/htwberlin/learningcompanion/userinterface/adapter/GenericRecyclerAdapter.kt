package de.htwberlin.learningcompanion.userinterface.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView

//adapter to fill the RecyclerView with goal data
class GenericRecyclerAdapter<T> (
        private val viewTypes: List<Class<out View>>
): androidx.recyclerview.widget.RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder<View>>() {

    constructor(vararg viewTypes: Class<out View>) : this(viewTypes.asList())

    var onItemClickListener: AdapterView.OnItemClickListener? = null

    var data: List<T> = emptyList()
        set(value) {
            field = value
            this@GenericRecyclerAdapter.notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ViewHolder<View>, position: Int) {

        if (holder.itemView::class.java == (data[position] as WidgetData).viewType) {
            (holder.itemView as Bindable<T>).onBind(data[position])

            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener {
                    onItemClickListener?.onItemClick(null, holder.itemView, position, (data[position] as WidgetData).id)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<View> {

//        val view = viewTypes[viewType].getMethod("build", Context::class.java).invoke(null, parent.context) as View
        val view = viewTypes[viewType].getConstructor(Context::class.java).newInstance(parent.context) as View

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder<V : View> (
            itemView : V
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


    }

    override fun getItemViewType(position: Int): Int {
        for ((index, type) in viewTypes.withIndex()) {
            if ((data[position] as WidgetData).viewType == type) {
                return index
            }
        }

        return super.getItemViewType(position)
    }
}