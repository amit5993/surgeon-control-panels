package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.databinding.RawLightCategoryBinding
import com.surgeon.controlpanels.model.LightCategoryModel


class LightCategoryAdapter(
    private val context: Activity, private val AdapterList: List<LightCategoryModel>
) : RecyclerView.Adapter<LightCategoryAdapter.ViewHolder>() {
    private var clickList: ClickLightCategoryInterface? = null

    interface ClickLightCategoryInterface {
        fun clickLightCategory(
            dataS: LightCategoryModel, position: Int
        )
    }

    fun registerInterface(c: ClickLightCategoryInterface?) {
        this.clickList = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RawLightCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return AdapterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapterData = AdapterList[position]
        holder.bind(adapterData)

        holder.itemView.setOnClickListener {

            for (i in AdapterList.indices) {
                AdapterList[i].isSelected = false
            }

            AdapterList[position].isSelected = true

            if (clickList != null) {
                clickList!!.clickLightCategory(adapterData, position)
            }
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: RawLightCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: LightCategoryModel) {
            binding.tvLightCategory.text = adapterData.name

            if (adapterData.isSelected) {
                //green
                binding.tvLightCategory.setBackgroundResource(R.drawable.shape_btn_white)
            } else {
                //red
                binding.tvLightCategory.setBackgroundResource(R.drawable.shape_border_white)
            }

        }
    }
}