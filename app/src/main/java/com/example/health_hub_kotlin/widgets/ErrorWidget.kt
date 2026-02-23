package com.example.health_hub_kotlin.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.health_hub_kotlin.databinding.WidgetErrorBinding

class ErrorWidget : Fragment() {

    private var _binding: WidgetErrorBinding? = null
    private val binding get() = _binding!!

    private var errorMessage: String? = null
    private var onRetry: (() -> Unit)? = null

    companion object {
        fun newInstance(error: String, retryAction: () -> Unit): ErrorWidget {
            return ErrorWidget().apply {
                errorMessage = error
                onRetry = retryAction
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WidgetErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvError.text = errorMessage ?: "Unknown error"

        binding.btnRetry.setOnClickListener {
            onRetry?.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}