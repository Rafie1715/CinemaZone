package com.dicoding.projekakhirplatformkelompok5.ui.orderticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.projekakhirplatformkelompok5.data.local.Order
import com.dicoding.projekakhirplatformkelompok5.databinding.ItemOrderHistoryBinding

class OrderHistoryAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]
        holder.bind(currentOrder)
        holder.itemView.setOnClickListener {
            onItemClick(currentOrder)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(private val binding: ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvOrderMovieTitle.text = order.movieTitle
            binding.tvOrderLocation.text = order.location
            binding.tvOrderShowDetails.text = "${order.showDate}, ${order.showTime} - ${order.studio}"
            binding.tvOrderSeatDetails.text = "Kursi: ${order.seat} (${order.quantity} Tiket)"
            binding.tvOrderDate.text = "Dipesan pada: ${order.orderDate}"
        }
    }
}