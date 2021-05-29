package com.appinbox.sdk.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.appinbox.sdk.R
import com.appinbox.sdk.databinding.CMessageBinding
import com.appinbox.sdk.databinding.FListBinding
import com.appinbox.sdk.model.Message
import com.appinbox.sdk.vm.ListVM
import com.appinbox.sdk.vm.STATUS

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListFragment : Fragment() {
    private var items: MutableList<Message> = mutableListOf()
    private var appId: String? = null
    private var appKey: String? = null
    private var contact: String? = null
    private val adapter = ItemAdapter(items)

    private var _binding: FListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMessageList.adapter = adapter
        loadPref()
        val model: ListVM by viewModels()
        model.init(appId, appKey, contact)
        model.getUsers().observe(this, {
            adapter.setData(it)
        })
        model.getStatus().observe(this, { status ->
            when (status) {
                STATUS.LOADING -> {
                    binding.vPullToRefresh.isRefreshing = true
                    binding.tvErrorText.visibility = View.GONE
                    binding.rvMessageList.visibility = View.VISIBLE
                }
                STATUS.DONE -> {
                    binding.vPullToRefresh.isRefreshing = false
                    binding.tvErrorText.visibility = View.GONE
                    binding.rvMessageList.visibility = View.VISIBLE
                }
                STATUS.FAIL -> {
                    binding.vPullToRefresh.isRefreshing = false
                    binding.tvErrorText.visibility = View.VISIBLE
                    binding.rvMessageList.visibility = View.GONE
                }
            }
        })
        binding.vPullToRefresh.setOnRefreshListener(model::loadMsgs)
    }

    private fun loadPref() {
        val preferences: SharedPreferences?=
            activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        appId = preferences?.getString(getString(R.string.sp_app), "")
        appKey = preferences?.getString(getString(R.string.sp_key), "")
        contact = preferences?.getString(getString(R.string.sp_contact), "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ItemAdapter(private val dataset: List<Message>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(_binding: CMessageBinding) : RecyclerView.ViewHolder(_binding.root) {
        private val binding: CMessageBinding = _binding

        fun bind(msg: Message) {
            binding.idText.text = msg.id
            binding.content.text = msg.title
            binding.details.text = msg.body
        }
    }

    private var _dataset = dataset
    fun setData(dataset: List<Message>) {
        this._dataset = dataset
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return _dataset.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
        val binding = CMessageBinding.inflate(adapterLayout, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = _dataset[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionShowDetails(item.id, item.title, item.body)
            it.findNavController().navigate(action)
        }
    }
}