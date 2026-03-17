package com.example.health_hub_kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.health_hub_kotlin.databinding.ItemPackageBinding
import com.example.health_hub_kotlin.models.Package

class PackageAdapter :
    ListAdapter<Package, PackageAdapter.PackageViewHolder>(DIFF_CALLBACK) {

    inner class PackageViewHolder(private val binding: ItemPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pkg: Package) {
            binding.tvPackageTitle.text = pkg.title
            binding.tvServiceCount.text = "${pkg.serviceCount} Services"
            binding.tvPackagePrice.text = "₹${pkg.price}"

            Glide.with(binding.root.context)
                .load(pkg.image)
                .into(binding.ivPackage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Package>() {

            override fun areItemsTheSame(oldItem: Package, newItem: Package): Boolean {
                return oldItem.id == newItem.id // ✅ unique ID comparison
            }

            override fun areContentsTheSame(oldItem: Package, newItem: Package): Boolean {
                return oldItem == newItem // ✅ full object comparison
            }
        }
    }


}