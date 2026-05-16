package com.example.legapaket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(
    private val list: List<ActivityModel>
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtResi: TextView =
            itemView.findViewById(R.id.txtResi)

        val txtDestination: TextView =
            itemView.findViewById(R.id.txtDestination)

        val txtStatus: TextView =
            itemView.findViewById(R.id.txtStatus)

        val txtTime: TextView =
            itemView.findViewById(R.id.txtTime)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_activity,
                parent,
                false
            )

        return ViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.txtResi.text = item.resi
        holder.txtDestination.text = item.destination
        holder.txtStatus.text = item.status
        holder.txtTime.text = item.time

    }

    override fun getItemCount(): Int {
        return list.size
    }

}