package com.example.legapaket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private val list: List<ReportModel>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private var expandedPosition = -1

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Summary
        val resi: TextView    = view.findViewById(R.id.tvResi)
        val status: TextView  = view.findViewById(R.id.tvStatus)
        val date: TextView    = view.findViewById(R.id.tvDate)

        // Detail
        val llDetail: View          = view.findViewById(R.id.ll_detail)
        val tvReceiver: TextView    = view.findViewById(R.id.tv_receiver_detail)
        val tvCity: TextView        = view.findViewById(R.id.tv_city_detail)
        val tvType: TextView        = view.findViewById(R.id.tv_type_detail)
        val tvWeight: TextView      = view.findViewById(R.id.tv_weight_detail)
        val tvPrice: TextView       = view.findViewById(R.id.tv_price_detail)
        val tvPayment: TextView     = view.findViewById(R.id.tv_payment_method)
        val ivPayment: ImageView    = view.findViewById(R.id.iv_payment_icon_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = list[position]
        val isExpanded = position == expandedPosition

        holder.resi.text   = item.resi
        holder.status.text = item.status
        holder.date.text   = item.date

        holder.tvReceiver.text = "Penerima: ${item.receiver}"
        holder.tvCity.text     = "Kota: ${item.city}"
        holder.tvType.text     = "Tipe: ${item.type}"
        holder.tvWeight.text   = "Berat: ${item.weight} Kg"
        holder.tvPrice.text    = "Ongkir: Rp ${String.format("%,d", item.price).replace(",", ".")}"
        holder.tvPayment.text  = item.paymentMethod

        val iconRes = when (item.paymentMethod) {
            "GoPay"     -> R.drawable.ic_gopay
            "DANA"      -> R.drawable.ic_dana
            "ShopeePay" -> R.drawable.ic_shopee
            "QRIS"      -> R.drawable.ic_qris
            else        -> R.drawable.ic_qris
        }
        holder.ivPayment.setImageResource(iconRes)

        if (isExpanded) {
            holder.llDetail.visibility = View.VISIBLE
            holder.llDetail.startAnimation(
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.expand)
            )
        } else {
            if (holder.llDetail.visibility == View.VISIBLE) {
                val anim = AnimationUtils.loadAnimation(
                    holder.itemView.context, R.anim.collapse
                )
                anim.setAnimationListener(object :
                    android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(a: android.view.animation.Animation?) {}
                    override fun onAnimationRepeat(a: android.view.animation.Animation?) {}
                    override fun onAnimationEnd(a: android.view.animation.Animation?) {
                        holder.llDetail.visibility = View.GONE
                    }
                })
                holder.llDetail.startAnimation(anim)
            } else {
                holder.llDetail.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            val prev = expandedPosition
            expandedPosition = if (isExpanded) -1 else position
            if (prev != -1) notifyItemChanged(prev)
            notifyItemChanged(expandedPosition)
        }
    }

    override fun getItemCount() = list.size
}