package com.example.health_hub_kotlin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.health_hub_kotlin.databinding.ItemSubcategoryBinding
import com.example.health_hub_kotlin.databinding.ItemSubcategoryGridBinding
import com.example.health_hub_kotlin.models.Subcategory

class SubcategoryAdapter(
    private var subcategories: List<Subcategory>,
    private val onItemClick: (Subcategory) -> Unit,
    initialViewType: ViewType = ViewType.LIST  // Add initial view type parameter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var currentViewType: ViewType = initialViewType
    private val LIST_VIEW_TYPE = 1
    private val GRID_VIEW_TYPE = 2

    init {
        Log.d("SubcategoryAdapter", "Adapter created with ${subcategories.size} items, view type: $currentViewType")
    }

    fun setViewType(viewType: ViewType) {
        if (currentViewType != viewType) {
            currentViewType = viewType
            notifyDataSetChanged()
            Log.d("SubcategoryAdapter", "View type changed to: $viewType")
        }
    }

    fun updateData(newSubcategories: List<Subcategory>) {
        subcategories = newSubcategories
        notifyDataSetChanged()
        Log.d("SubcategoryAdapter", "Data updated with ${newSubcategories.size} items")
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentViewType == ViewType.LIST) LIST_VIEW_TYPE else GRID_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LIST_VIEW_TYPE -> {
                Log.d("SubcategoryAdapter", "Creating LIST view holder")
                val binding = ItemSubcategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ListViewHolder(binding)
            }
            else -> {
                Log.d("SubcategoryAdapter", "Creating GRID view holder")
                val binding = ItemSubcategoryGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GridViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val subcategory = subcategories[position]
        when (holder) {
            is ListViewHolder -> holder.bind(subcategory)
            is GridViewHolder -> holder.bind(subcategory)
        }
    }

    override fun getItemCount() = subcategories.size

    inner class ListViewHolder(private val binding: ItemSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subcategory: Subcategory) {
            binding.tvTitle.text = subcategory.title
            binding.tvPrice.text = subcategory.price
            binding.tvDuration.text = subcategory.duration
            binding.tvTag.text = subcategory.tag
            binding.tvRating.text = "${subcategory.rating} ★"
            binding.tvDescription.text = subcategory.description

            Glide.with(binding.root.context)
                .load(subcategory.image)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.ivSubcategory)

            binding.root.setOnClickListener {
                onItemClick(subcategory)
            }
        }
    }

    inner class GridViewHolder(private val binding: ItemSubcategoryGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subcategory: Subcategory) {
            binding.tvTitle.text = subcategory.title
            binding.tvPrice.text = subcategory.price
            binding.tvDuration.text = subcategory.duration
            binding.tvTag.text = subcategory.tag
            binding.tvRating.text = "${subcategory.rating} ★"
            binding.tvDescription.text = subcategory.description

            Glide.with(binding.root.context)
                .load(subcategory.image)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.ivSubcategory)

            binding.root.setOnClickListener {
                onItemClick(subcategory)
            }
        }
    }
}