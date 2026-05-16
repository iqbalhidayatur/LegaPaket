package com.example.legapaket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private val list: List<ReportModel>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resi: TextView = view.findViewById(R.id.tvResi)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val date: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = list[position]
        holder.resi.text = item.resi
        holder.status.text = item.status
        holder.date.text = item.date
    }

    override fun getItemCount() = list.size

}