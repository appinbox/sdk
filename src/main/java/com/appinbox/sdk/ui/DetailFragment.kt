package com.appinbox.sdk.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.appinbox.sdk.databinding.FDetailBinding
import com.appinbox.sdk.repo.svc.ApiBuilder

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DetailFragment : Fragment() {

    private var _binding: FDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDetailTitle.text = args.title
        binding.tvDetailBody.text = args.body
        binding.tvDetailSentAt.text = args.sentAt
        binding.tvDetailReadAt.text = args.readAt
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}