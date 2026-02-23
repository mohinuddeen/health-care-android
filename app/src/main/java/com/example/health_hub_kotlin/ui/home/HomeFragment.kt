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
import com.example.health_hub_kotlin.models.TrendingService
import com.example.health_hub_kotlin.ui.category.CategoryDetailFragment
import com.example.health_hub_kotlin.viewmodel.HomeState
import com.example.health_hub_kotlin.viewmodel.HomeViewModel

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
        trendingAdapter = TrendingServiceAdapter(emptyList()) { service ->
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
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                HomeState.LOADING -> showLoading()
                HomeState.LOADED -> showContent()
                HomeState.ERROR -> showError()
                HomeState.REFRESHING -> showRefreshing()
            }
        }

        viewModel.homeData.observe(viewLifecycleOwner) { homeData ->
            updateUI(homeData)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.tvError.text = it
            }
        }

        viewModel.showLongLoading.observe(viewLifecycleOwner) { showLong ->
            binding.tvLongLoading.visibility = if (showLong) View.VISIBLE else View.GONE
        }
    }

    private fun updateUI(homeData: com.example.health_hub_kotlin.models.HomeResponse) {
        // Update banners
        bannerAdapter = BannerAdapter(homeData.banners)
        binding.rvBanners.adapter = bannerAdapter

        // Update categories
        categoryAdapter = CategoryAdapter(homeData.categories) { category ->
            navigateToCategoryDetail(category)
        }
        binding.rvCategories.adapter = categoryAdapter

        // Update features
        featureAdapter = FeatureAdapter(homeData.features)
        binding.rvFeatures.adapter = featureAdapter

        // Update trending services
        trendingAdapter = TrendingServiceAdapter(homeData.trendingServices) { service ->
            handleBookService(service)
        }
        binding.rvTrendingServices.adapter = trendingAdapter

        // Update packages
        packageAdapter = PackageAdapter(homeData.packages)
        binding.rvPackages.adapter = packageAdapter
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