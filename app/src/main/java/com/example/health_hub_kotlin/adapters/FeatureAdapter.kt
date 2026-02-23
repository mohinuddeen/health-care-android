package com.example.health_hub_kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.health_hub_kotlin.databinding.ItemFeatureBinding
import com.example.health_hub_kotlin.models.Feature

class FeatureAdapter(private val features: List<Feature>) :
    RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder>() {

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
        holder.bind(features[position])
    }

    override fun getItemCount() = features.size
}