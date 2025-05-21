package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.common.formatStopwatchTime
import com.surgeon.controlpanels.databinding.RawLapBinding
import com.surgeon.controlpanels.model.LapModel

class StopwatchAdapter(
    private val context: Activity, private var AdapterList: List<LapModel>
) : RecyclerView.Adapter<StopwatchAdapter.ViewHolder>() {
    private var clickList: ClickLapInterface? = null

    interface ClickLapInterface {
        fun clickLap(
            dataS: LapModel, position: Int
        )
    }

    fun registerInterface(c: ClickLapInterface?) {
        this.clickList = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawLapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return AdapterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapterData = AdapterList[position]
        holder.bind(adapterData)

        holder.itemView.setOnClickListener {
            if (clickList != null) {
                clickList!!.clickLap(adapterData, position)
            }
        }
    }

    inner class ViewHolder(private val binding: RawLapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: LapModel) {
            binding.tvLap.text = adapterData.lapTime.formatStopwatchTime(false)
            binding.tvTotalLap.text = adapterData.totalTime.formatStopwatchTime(false)
            binding.tvId.text = "Lap ${adapterData.id}"
        }
    }

}