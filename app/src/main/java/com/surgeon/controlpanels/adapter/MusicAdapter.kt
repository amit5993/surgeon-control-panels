package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.databinding.RawMusicBinding


class MusicAdapter(private val context: Activity, private val AdapterList: List<String>) :
    RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    private var clickList: ClickMusicInterface? = null

    var filteredList: List<String> = AdapterList

    interface ClickMusicInterface {
        fun clickMusic(
            dataS: String, position: Int
        )
    }

    fun registerInterface(c: ClickMusicInterface?) {
        this.clickList = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapterData = filteredList[position]
        holder.bind(adapterData)

        holder.itemView.setOnClickListener {
            if (clickList != null) {
                clickList!!.clickMusic(adapterData, position)
            }
        }
    }

    inner class ViewHolder(private val binding: RawMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: String) {
            binding.tvName.text = adapterData.substring(adapterData.lastIndexOf("/") + 1)
        }
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            AdapterList
        } else {
            AdapterList.filter { it.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}