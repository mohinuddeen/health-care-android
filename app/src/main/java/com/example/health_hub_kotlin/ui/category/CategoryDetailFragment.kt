package com.example.health_hub_kotlin.ui.category

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.health_hub_kotlin.R
import com.example.health_hub_kotlin.adapters.SubcategoryAdapter
import com.example.health_hub_kotlin.adapters.ViewType
import com.example.health_hub_kotlin.databinding.FragmentCategoryDetailBinding
import com.example.health_hub_kotlin.ui.service.ServiceDetailFragment
import com.example.health_hub_kotlin.viewmodel.CategoryState
import com.example.health_hub_kotlin.viewmodel.CategoryViewModel
import com.example.health_hub_kotlin.widgets.ErrorWidget
import com.example.health_hub_kotlin.widgets.LoadingWidget

class CategoryDetailFragment : Fragment() {

    private var _binding: FragmentCategoryDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategoryViewModel
    private lateinit var subcategoryAdapter: SubcategoryAdapter

    private var categoryId: Int = 0
    private var categoryTitle: String = ""
    private var currentViewType: ViewType = ViewType.LIST

    companion object {
        private const val ARG_CATEGORY_ID = "category_id"
        private const val ARG_CATEGORY_TITLE = "category_title"
        private const val TAG = "CategoryDetailFragment"
        private const val PREFS_NAME = "category_prefs"
        private const val KEY_VIEW_TYPE = "view_type"

        fun newInstance(categoryId: Int, categoryTitle: String): CategoryDetailFragment {
            return CategoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_ID, categoryId)
                    putString(ARG_CATEGORY_TITLE, categoryTitle)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getInt(ARG_CATEGORY_ID)
            categoryTitle = it.getString(ARG_CATEGORY_TITLE) ?: ""
        }

        // Important: Tell the fragment that it has options menu
        setHasOptionsMenu(true)

        // Load saved view preference
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0)
        currentViewType = if (prefs.getBoolean(KEY_VIEW_TYPE, true)) {
            ViewType.LIST
        } else {
            ViewType.GRID
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()
        updateToolbarMenuIcon()

        // Fetch data
        viewModel.fetchCategoryData(categoryId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.category_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        // Set initial icon based on current view type
        val menuItem = menu.findItem(R.id.action_toggle_view)
        updateMenuIcon(menuItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_view -> {
                toggleViewType()
                updateMenuIcon(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateMenuIcon(menuItem: MenuItem) {
        val iconRes = if (currentViewType == ViewType.LIST) {
            R.drawable.ic_grid_view  // Show grid icon when in list mode (to switch to grid)
        } else {
            R.drawable.ic_list_view   // Show list icon when in grid mode (to switch to list)
        }
        menuItem.setIcon(iconRes)
        menuItem.title = if (currentViewType == ViewType.LIST) "Grid view" else "List view"
    }

    private fun toggleViewType() {
        currentViewType = if (currentViewType == ViewType.LIST) ViewType.GRID else ViewType.LIST

        // Save preference
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, 0)
        prefs.edit().putBoolean(KEY_VIEW_TYPE, currentViewType == ViewType.LIST).apply()

        // Update layout manager first
        updateLayoutManager()

        // Then update adapter view type
        subcategoryAdapter.setViewType(currentViewType)

        Log.d(TAG, "View type toggled to: $currentViewType")
    }

    private fun updateLayoutManager() {
        val newLayoutManager = if (currentViewType == ViewType.LIST) {
            LinearLayoutManager(requireContext())
        } else {
            GridLayoutManager(requireContext(), 2)
        }

        // Set the new layout manager
        binding.rvSubcategories.layoutManager = newLayoutManager

        // Force a layout update
        binding.rvSubcategories.requestLayout()
    }

//    private fun updateLayoutManager() {
//        binding.rvSubcategories.layoutManager = if (currentViewType == ViewType.LIST) {
//            LinearLayoutManager(requireContext())
//        } else {
//            GridLayoutManager(requireContext(), 2)
//        }
//    }

    private fun setupToolbar() {
        // Set the toolbar as the action bar for the fragment
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // Enable the back button
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.title = categoryTitle
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Important: Tell the toolbar that we have an options menu
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            onOptionsItemSelected(menuItem)
        }
    }
    private fun updateToolbarMenuIcon() {
        val menuItem = binding.toolbar.menu.findItem(R.id.action_toggle_view)
        if (menuItem != null) {
            val iconRes = if (currentViewType == ViewType.LIST) {
                R.drawable.ic_grid_view  // Show grid icon when in list mode
            } else {
                R.drawable.ic_list_view   // Show list icon when in grid mode
            }
            menuItem.setIcon(iconRes)
            menuItem.title = if (currentViewType == ViewType.LIST) "Switch to grid" else "Switch to list"

            Log.d(TAG, "Menu icon updated for view type: $currentViewType")
        }
    }
    private fun setupRecyclerView() {
        // Pass the currentViewType to the adapter
        subcategoryAdapter = SubcategoryAdapter(
            subcategories = emptyList(),
            onItemClick = { subcategory ->
                navigateToServiceDetail(subcategory)
            },
            initialViewType = currentViewType  // Pass the saved view type
        )

        updateLayoutManager()
        binding.rvSubcategories.adapter = subcategoryAdapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchCategoryData(categoryId)
        }
        binding.swipeRefreshLayout.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "State changed: $state")
            when (state) {
                CategoryState.LOADING -> showLoading()
                CategoryState.LOADED -> showContent()
                CategoryState.ERROR -> showError()
            }
        }

        viewModel.categoryData.observe(viewLifecycleOwner) { categoryData ->
            Log.d(TAG, "Data received: ${categoryData?.subcategories?.size} items")
            categoryData?.let {
                // Update adapter with new data
                subcategoryAdapter.updateData(it.subcategories)

                // Double-check that the view type is correct after data load
                // This ensures the layout is correct even if something went wrong
                binding.rvSubcategories.post {
                    subcategoryAdapter.setViewType(currentViewType)
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.e(TAG, "Error: $errorMessage")
        }
    }
    private fun showLoading() {
        binding.swipeRefreshLayout.visibility = View.GONE

        val loadingWidget = LoadingWidget()
        childFragmentManager.beginTransaction()
            .replace(R.id.container, loadingWidget)
            .commitAllowingStateLoss()
    }

    private fun showContent() {
        childFragmentManager.beginTransaction()
            .replace(R.id.container, Fragment())
            .commitAllowingStateLoss()

        binding.swipeRefreshLayout.visibility = View.VISIBLE
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showError() {
        val errorMessage = viewModel.error.value ?: "Unknown error"

        binding.swipeRefreshLayout.visibility = View.GONE

        val errorWidget = ErrorWidget.newInstance(errorMessage) {
            viewModel.fetchCategoryData(categoryId)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.container, errorWidget)
            .commitAllowingStateLoss()
    }

    private fun navigateToServiceDetail(subcategory: com.example.health_hub_kotlin.models.Subcategory) {
        Log.d(TAG, "Navigating to service detail: ${subcategory.title}")

        val fragment = ServiceDetailFragment.newInstance(subcategory.id, subcategory.title)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearData()
        _binding = null
    }
}