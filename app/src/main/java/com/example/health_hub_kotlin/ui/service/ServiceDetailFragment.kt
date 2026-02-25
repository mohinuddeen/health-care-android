package com.example.health_hub_kotlin.ui.service

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.health_hub_kotlin.R
import com.example.health_hub_kotlin.databinding.FragmentServiceDetailBinding
import com.example.health_hub_kotlin.models.Service
import com.example.health_hub_kotlin.viewmodel.ServiceState
import com.example.health_hub_kotlin.viewmodel.ServiceViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ServiceDetailFragment : Fragment() {

    private var _binding: FragmentServiceDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ServiceViewModel

    private var serviceId: Int = 0
    private var serviceTitle: String = ""

    companion object {
        private const val ARG_SERVICE_ID = "service_id"
        private const val ARG_SERVICE_TITLE = "service_title"
        private const val TAG = "ServiceDetailFragment"

        fun newInstance(serviceId: Int, serviceTitle: String): ServiceDetailFragment {
            return ServiceDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SERVICE_ID, serviceId)
                    putString(ARG_SERVICE_TITLE, serviceTitle)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = it.getInt(ARG_SERVICE_ID)
            serviceTitle = it.getString(ARG_SERVICE_TITLE) ?: ""
            Log.d(TAG, "ServiceDetailFragment created for ID: $serviceId, Title: $serviceTitle")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ServiceViewModel::class.java]

        setupToolbar()
        setupObservers()
        setupRetryButton()

        // Fetch data
        Log.d(TAG, "Calling fetchServiceData for ID: $serviceId")
        viewModel.fetchServiceData(serviceId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            Log.d(TAG, "Retry button clicked")
            viewModel.fetchServiceData(serviceId)
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "State changed: $state")
            when (state) {
                ServiceState.LOADING -> showLoading()
                ServiceState.LOADED -> showContent()
                ServiceState.ERROR -> showError()
            }
        }

        viewModel.serviceData.observe(viewLifecycleOwner) { service ->
            if (service != null) {
                Log.d(TAG, "Service data received in observer: ${service.title}")
                bindData(service)
            } else {
                Log.d(TAG, "Service data is null")
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.e(TAG, "Error: $errorMessage")
            binding.tvError.text = errorMessage ?: "Failed to load service details"
        }
    }

    private fun showLoading() {
        Log.d(TAG, "Showing loading state")
        binding.loadingLayout.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showContent() {
        Log.d(TAG, "Showing content state")
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showError() {
        Log.d(TAG, "Showing error state")
        binding.loadingLayout.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
    }

    private fun bindData(service: Service) {
        Log.d(TAG, "Binding data for service: ${service.title}")

        try {
            // Set title
            binding.tvServiceTitle.text = service.title
            binding.tvServiceSubtitle.text = service.subtitle
            binding.tvRating.text = String.format("%.1f", service.rating)

            // Load image
            Glide.with(requireContext())
                .load(service.image)
                .centerCrop()
                .placeholder(R.drawable.ic_medical_services)
                .error(R.drawable.ic_medical_services)
                .into(binding.ivServiceImage)

            // Price and duration
            binding.tvPrice.text = "$${service.price}"
            binding.tvDuration.text = service.duration

            // Description
            binding.tvDescription.text = service.description

            // Tests Include
            if (service.testsInclude.isNotEmpty()) {
                binding.testsIncludeLayout.visibility = View.VISIBLE
                binding.testsIncludeContainer.removeAllViews()
                service.testsInclude.forEach { test ->
                    binding.testsIncludeContainer.addView(createListItem(test))
                }
            } else {
                binding.testsIncludeLayout.visibility = View.GONE
            }

            // Benefits
            if (service.benefits.isNotEmpty()) {
                binding.benefitsLayout.visibility = View.VISIBLE
                binding.benefitsContainer.removeAllViews()
                service.benefits.forEach { benefit ->
                    binding.benefitsContainer.addView(createListItem(benefit))
                }
            } else {
                binding.benefitsLayout.visibility = View.GONE
            }

            // Suitable For
            if (service.suitableFor.isNotEmpty()) {
                binding.suitableForLayout.visibility = View.VISIBLE
                binding.suitableForChipGroup.removeAllViews()
                service.suitableFor.forEach { item ->
                    binding.suitableForChipGroup.addView(createChip(item))
                }
            } else {
                binding.suitableForLayout.visibility = View.GONE
            }

            // Key Ingredients (if available)
            if (service.keyIngredients.isNotEmpty()) {
                binding.keyIngredientsLayout.visibility = View.VISIBLE
                binding.keyIngredientsContainer.removeAllViews()
                service.keyIngredients.forEach { ingredient ->
                    binding.keyIngredientsContainer.addView(createListItem(ingredient))
                }
            } else {
                binding.keyIngredientsLayout.visibility = View.GONE
            }

            // Info cards
            binding.tvFastingRequired.text = service.fastingRequired
            binding.tvReportTime.text = service.reportTime
            binding.tvPreparation.text = service.preparation

            // Book button
            binding.btnBook.setOnClickListener {
                showBookingConfirmation(service)
            }

            Log.d(TAG, "Data binding completed successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error binding data: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun createListItem(text: String): View {
        val view = layoutInflater.inflate(R.layout.item_list_with_check, binding.benefitsContainer, false)
        val tvText = view.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_text)
        tvText.text = text
        return view
    }

    private fun createChip(text: String): View {
        val chip = layoutInflater.inflate(R.layout.item_chip, binding.suitableForChipGroup, false) as com.google.android.material.chip.Chip
        chip.text = text
        return chip
    }

    private fun showBookingConfirmation(service: Service) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_booking_confirmation, null)

        view.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tv_service_title).text = service.title
        view.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_done).setOnClickListener {
            bottomSheetDialog.dismiss()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}