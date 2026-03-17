package com.example.health_hub_kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.health_hub_kotlin.databinding.ItemFeatureBinding
import com.example.health_hub_kotlin.models.Feature

class FeatureAdapter :
    ListAdapter<Feature, FeatureAdapter.FeatureViewHolder>(DIFF_CALLBACK) {

    inner class FeatureViewHolder(private val binding: ItemFeatureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(feature: Feature) {
            binding.tvFeatureIcon.text = feature.icon
            binding.tvFeatureTitle.text = feature.title
            binding.tvFeatureSubtitle.text = feature.subtitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {
        val binding = ItemFeatureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Feature>() {

            override fun areItemsTheSame(oldItem: Feature, newItem: Feature): Boolean {
                return oldItem.id == newItem.id // ✅ unique ID comparison
            }

            override fun areContentsTheSame(oldItem: Feature, newItem: Feature): Boolean {
                return oldItem == newItem // ✅ full object comparison
            }
        }
    }

}