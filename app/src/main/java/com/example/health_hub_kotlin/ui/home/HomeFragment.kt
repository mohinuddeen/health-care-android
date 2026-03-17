package com.example.health_hub_kotlin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.health_hub_kotlin.R
import com.example.health_hub_kotlin.adapters.*
import com.example.health_hub_kotlin.databinding.FragmentHomeBinding
import com.example.health_hub_kotlin.models.Category
import com.example.health_hub_kotlin.models.HomeResponse
import com.example.health_hub_kotlin.models.TrendingService
import com.example.health_hub_kotlin.ui.category.CategoryDetailFragment
import com.example.health_hub_kotlin.viewmodel.HomeViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var featureAdapter: FeatureAdapter
    private lateinit var trendingAdapter: TrendingServiceAdapter
    private lateinit var packageAdapter: PackageAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        // light status bar

        setupRecyclerViews()
        setupClickListeners()
        setupObservers()
        setupSwipeRefresh()
    }

    private fun setupRecyclerViews() {
        // Banner RecyclerView
        bannerAdapter = BannerAdapter(emptyList())
        binding.rvBanners.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bannerAdapter
        }

        // Category RecyclerView
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            navigateToCategoryDetail(category)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // Features RecyclerView
        featureAdapter = FeatureAdapter(emptyList())
        binding.rvFeatures.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = featureAdapter
        }

        // Trending Services RecyclerView
        trendingAdapter = TrendingServiceAdapter { service ->
            handleBookService(service)
        }
        binding.rvTrendingServices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trendingAdapter
        }

        // Packages RecyclerView
        packageAdapter = PackageAdapter(emptyList())
        binding.rvPackages.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = packageAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnRetry.setOnClickListener {
            viewModel.fetchHomeData()
        }

        binding.ivNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Notifications", Toast.LENGTH_SHORT).show()
        }

        binding.btnSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Search clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun setupObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collect { state ->

                    // Loading
                    binding.loadingLayout.visibility =
                        if (state.isLoading) View.VISIBLE else View.GONE

                    // Content visibility
                    binding.contentLayout.visibility =
                        if (!state.isLoading && state.data != null) View.VISIBLE else View.GONE

                    // Error visibility
                    binding.errorLayout.visibility =
                        if (state.error != null) View.VISIBLE else View.GONE

                    // Swipe refresh
                    binding.swipeRefreshLayout.isRefreshing = state.isRefreshing

                    // Long loading text
                    binding.tvLongLoading.visibility =
                        if (state.showLongLoading) View.VISIBLE else View.GONE

                    // Error text
                    state.error?.let {
                        binding.tvError.text = it
                    }

                    // Update data if available
                    state.data?.let {
                        updateUI(it)
                    }
                }
            }
        }
    }

    private fun updateUI(homeData: HomeResponse) {
        bannerAdapter.updateData(homeData.banners)
        categoryAdapter.updateData(homeData.categories)
        featureAdapter.updateData(homeData.features)
        trendingAdapter.submitList(homeData.trendingServices)
        packageAdapter.updateData(homeData.packages)
    }

    private fun navigateToCategoryDetail(category: com.example.health_hub_kotlin.models.Category) {
        val fragment = CategoryDetailFragment.newInstance(category.id, category.title)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleBookService(service: TrendingService) {
        Toast.makeText(requireContext(), "Booking: ${service.title}", Toast.LENGTH_SHORT).show()
        // TODO: Navigate to booking screen
    }

    private fun showLoading() {
        binding.loadingLayout.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
        binding.swipeRefreshLayout.isEnabled = false
    }

    private fun showContent() {
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        binding.swipeRefreshLayout.isEnabled = true
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showError() {
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.swipeRefreshLayout.isEnabled = false
    }

    private fun showRefreshing() {
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        binding.swipeRefreshLayout.isRefreshing = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}