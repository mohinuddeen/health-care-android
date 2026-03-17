package com.example.health_hub_kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.health_hub_kotlin.databinding.ItemBannerBinding
import com.example.health_hub_kotlin.models.Banner

class BannerAdapter
    : ListAdapter<Banner, BannerAdapter.BannerViewHolder>(DIFF_CALLBACK) {

    inner class BannerViewHolder(private val binding: ItemBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(banner: Banner) {
            Glide.with(binding.root.context)
                .load(banner.image)
                .centerCrop()
                .into(binding.ivBanner)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Banner>() {

            override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean {
                return oldItem.id == newItem.id // ✅ unique ID comparison
            }

            override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean {
                return oldItem == newItem // ✅ full object comparison
            }
        }
    }
}