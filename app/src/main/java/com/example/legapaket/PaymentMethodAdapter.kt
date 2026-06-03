package com.example.legapaket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

data class PaymentMethod(
    val name: String,
    val iconResId: Int
)

class PaymentMethodAdapter(
    context: Context,
    private val methods: List<PaymentMethod>
) : ArrayAdapter<PaymentMethod>(context, 0, methods) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_payment_dropdown, parent, false)
        val method = methods[position]
        view.findViewById<ImageView>(R.id.iv_payment_icon).setImageResource(method.iconResId)
        view.findViewById<TextView>(R.id.tv_payment_name).text = method.name
        return view
    }
}