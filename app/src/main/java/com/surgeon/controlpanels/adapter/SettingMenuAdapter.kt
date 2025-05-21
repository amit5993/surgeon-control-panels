package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.databinding.RawSettingMenuBinding
import com.surgeon.controlpanels.model.SettingMenuModel


class SettingMenuAdapter(
    private val context: Activity, private val AdapterList: List<SettingMenuModel>
) : RecyclerView.Adapter<SettingMenuAdapter.ViewHolder>() {
    private var clickList: ClickSettingMenuInterface? = null

    interface ClickSettingMenuInterface {
        fun clickSettingMenu(
            dataS: SettingMenuModel, position: Int
        )
    }

    fun registerInterface(c: ClickSettingMenuInterface?) {
        this.clickList = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RawSettingMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                clickList!!.clickSettingMenu(adapterData, position)
            }
        }
    }

    inner class ViewHolder(private val binding: RawSettingMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: SettingMenuModel) {
            binding.tvName.text = adapterData.name

            if (adapterData.isSelected) {
//            holder.llMain.setBackgroundResource(R.color.white)
                binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvName.setBackgroundResource(R.drawable.shape_round_selected)

            } else {
//            holder.llMain.setBackgroundResource(R.color.colorAppLight)
                binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.colorGray))
                binding.tvName.setBackgroundResource(0)

            }

        }
    }
}