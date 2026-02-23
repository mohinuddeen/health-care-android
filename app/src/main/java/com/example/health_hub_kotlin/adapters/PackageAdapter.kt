package com.example.health_hub_kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.health_hub_kotlin.databinding.ItemPackageBinding
import com.example.health_hub_kotlin.models.Package

class PackageAdapter(private val packages: List<Package>) :
    RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

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
        holder.bind(packages[position])
    }

    override fun getItemCount() = packages.size
}