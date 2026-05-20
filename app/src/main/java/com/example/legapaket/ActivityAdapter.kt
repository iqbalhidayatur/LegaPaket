package com.example.legapaket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActivityAdapter(
    private val list: List<ActivityModel>
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    private var expandedPosition = -1  // ← track posisi yang sedang terbuka

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Summary row (selalu tampil)
        val txtResi: TextView        = itemView.findViewById(R.id.txtResi)
        val txtDestination: TextView = itemView.findViewById(R.id.txtDestination)
        val txtStatus: TextView      = itemView.findViewById(R.id.txtStatus)
        val txtTime: TextView        = itemView.findViewById(R.id.txtTime)

        // Detail section (toggle)
        val llDetail: View           = itemView.findViewById(R.id.ll_detail)
        val tvType: TextView         = itemView.findViewById(R.id.tv_package_type)
        val tvCost: TextView         = itemView.findViewById(R.id.tv_cost)
        val tvPaymentMethod: TextView= itemView.findViewById(R.id.tv_payment_method)
        val ivPaymentIcon: ImageView = itemView.findViewById(R.id.iv_payment_icon_detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val isExpanded = position == expandedPosition

        // Isi data summary
        holder.txtResi.text        = item.resi
        holder.txtDestination.text = item.destination
        holder.txtStatus.text      = item.status
        holder.txtTime.text        = item.time

        // Isi data detail
        holder.tvType.text = "Tipe pengiriman: ${item.type}"
        holder.tvCost.text         = "Ongkir: Rp ${item.price}"
        holder.tvPaymentMethod.text = item.paymentMethod

        // Set icon metode pembayaran
        val iconRes = when (item.paymentMethod) {
            "GoPay"     -> R.drawable.ic_gopay
            "DANA"      -> R.drawable.ic_dana
            "ShopeePay" -> R.drawable.ic_shopee
            "QRIS"      -> R.drawable.ic_qris
            else        -> R.drawable.ic_qris
        }
        holder.ivPaymentIcon.setImageResource(iconRes)

        // Tampilkan atau sembunyikan detail dengan animasi
        if (isExpanded) {
            holder.llDetail.visibility = View.VISIBLE
            holder.llDetail.startAnimation(
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.expand)
            )
        } else {
            if (holder.llDetail.visibility == View.VISIBLE) {
                val anim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.collapse)
                anim.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
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

        // Klik card untuk toggle expand/collapse
        holder.itemView.setOnClickListener {
            val prev = expandedPosition
            expandedPosition = if (isExpanded) -1 else position
            if (prev != -1) notifyItemChanged(prev)
            notifyItemChanged(expandedPosition)
        }
    }

    override fun getItemCount() = list.size
}