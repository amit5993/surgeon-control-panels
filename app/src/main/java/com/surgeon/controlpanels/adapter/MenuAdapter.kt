package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.common.Constant
import com.surgeon.controlpanels.common.loadResImage
import com.surgeon.controlpanels.databinding.RawMenuBinding
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.model.MenuModel


class MenuAdapter(
    private val context: Activity, private var AdapterList: List<MenuModel>
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private var clickList: ClickMenuInterface? = null

    interface ClickMenuInterface {
        fun clickMenu(
            dataS: MenuModel, position: Int
        )
    }

    fun registerInterface(c: ClickMenuInterface?) {
        this.clickList = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                clickList!!.clickMenu(adapterData, position)
            }
        }
    }

    inner class ViewHolder(private val binding: RawMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: MenuModel) {
            binding.tvName.text = adapterData.title

            if (adapterData.desc.isEmpty()) {
                context.loadResImage(adapterData.img, binding.img)

                binding.img.visibility = View.VISIBLE
                binding.tvDesc.visibility = View.GONE
            } else {
                binding.tvDesc.text = adapterData.desc

                binding.img.visibility = View.GONE
                binding.tvDesc.visibility = View.VISIBLE
            }


            if (adapterData.click == Constant.mgps) {

                val dbHelper = DbHelper(context)
                val list = dbHelper.getDeviceSettingsByType(1)
                val index = list.indexOfFirst { it.value == "1" }
                if (index != -1) {
                    binding.img.setColorFilter(ContextCompat.getColor(context, R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN)
                    binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                } else {
                    binding.img.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
                    binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            } else {
                binding.img.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
                binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

        }
    }

}