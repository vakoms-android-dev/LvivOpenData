package com.vakoms.oleksandr.havruliyk.lvivopendata.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vakoms.oleksandr.havruliyk.lvivopendata.R
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.catering.CateringRecord
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.listener.OnItemClickListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list.*

class CateringAdapter(var onClickListener: OnItemClickListener) : RecyclerView.Adapter<CateringAdapter.ViewHolder>() {

    var data: List<CateringRecord> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position], onClickListener)

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(item: CateringRecord, onClickListener: OnItemClickListener) {
            itemView.setOnClickListener { onClickListener.onItemClick(itemView, position) }

            label_view.text = with(item) { "$name $street" }
            address_view.text = with(item) { "$street $building" }
        }
    }
}