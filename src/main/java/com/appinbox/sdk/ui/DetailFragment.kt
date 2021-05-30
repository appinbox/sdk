package com.appinbox.sdk.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.appinbox.sdk.databinding.FDetailBinding
import com.appinbox.sdk.model.SdkAuth
import com.appinbox.sdk.repo.dao.Message
import com.appinbox.sdk.repo.svc.ApiBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

        Handler().postDelayed({
            ApiBuilder.getApi().readMessage(SdkAuth.appId, SdkAuth.appKey, SdkAuth.contact, args.messageId)
                .enqueue(object : Callback<Message> {
                    override fun onResponse(call: Call<Message>, response: Response<Message>) {}
                    override fun onFailure(call: Call<Message>, t: Throwable?) {}
                })
        }, 5000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}