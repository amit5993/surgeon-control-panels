package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.databinding.RawColorThemeBinding
import com.surgeon.controlpanels.model.ColorThemeModel


class ColorThemeAdapter(
    private val context: Activity, private val AdapterList: List<ColorThemeModel>
) : RecyclerView.Adapter<ColorThemeAdapter.ViewHolder>() {
    private var clickList: ClickColorThemeInterface? = null

    interface ClickColorThemeInterface {
        fun clickColorTheme(
            dataS: ColorThemeModel, position: Int
        )
    }

    fun registerInterface(c: ClickColorThemeInterface?) {
        this.clickList = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RawColorThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                clickList!!.clickColorTheme(adapterData, position)
            }
        }
    }

    inner class ViewHolder(private val binding: RawColorThemeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: ColorThemeModel) {
            binding.tvMenuName.text = adapterData.menuName
            binding.imdColorPicker.setOnClickListener {

            }
        }
    }
}