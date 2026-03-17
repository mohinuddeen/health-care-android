package com.example.health_hub_kotlin.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.health_hub_kotlin.databinding.ItemTrendingServiceBinding
import com.example.health_hub_kotlin.models.TrendingService

class TrendingServiceAdapter(
    private val onBookClick: (TrendingService) -> Unit
) : ListAdapter<TrendingService, TrendingServiceAdapter.TrendingServiceViewHolder>(DiffCallback()) {

    inner class TrendingServiceViewHolder(private val binding: ItemTrendingServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(service: TrendingService) {
            binding.tvServiceTitle.text = service.title
            binding.tvOriginalPrice.text = "₹${service.price}"
            binding.tvDiscountedPrice.text = "₹${service.discountPrice}"
            binding.tvRating.text = "${service.rating} (${service.reviews}+)"
            binding.tvOriginalPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

            binding.btnBook.setOnClickListener {
                onBookClick(service)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingServiceViewHolder {
        val binding = ItemTrendingServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrendingServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TrendingService>() {
        override fun areItemsTheSame(
            oldItem: TrendingService,
            newItem: TrendingService
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TrendingService,
            newItem: TrendingService
        ): Boolean {
            return oldItem == newItem
        }
    }
}