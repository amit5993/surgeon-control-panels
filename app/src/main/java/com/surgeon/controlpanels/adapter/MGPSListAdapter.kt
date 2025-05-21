package com.surgeon.controlpanels.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.databinding.RawMgpsBinding
import com.surgeon.controlpanels.model.dbsokect.DeviceSettingModel


class MGPSListAdapter(var context: Context, var AdapterList: List<DeviceSettingModel>) :
    RecyclerView.Adapter<MGPSListAdapter.ViewHolder>() {
    private var clickList: ClickPatientInterface? = null


    interface ClickPatientInterface {
        fun clickMGPS(
            dataS: DeviceSettingModel, position: Int
        )
    }

    fun registerInterface(c: ClickPatientInterface?) {
        this.clickList = c
    }

    fun updateData(connectors: MutableList<DeviceSettingModel>?) {
        AdapterList = connectors!!
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawMgpsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return AdapterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapterData = AdapterList[position]
        holder.bind(adapterData)

        holder.itemView.setOnClickListener {

//            for (i in AdapterList.indices) {
//                AdapterList[i].isSelected = false
//            }
//            AdapterList[position].isSelected = true
//            notifyDataSetChanged()
            if (clickList != null) {
                clickList!!.clickMGPS(adapterData, position)
            }

        }
    }

    inner class ViewHolder(private val binding: RawMgpsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: DeviceSettingModel) {
            binding.tvTitle.text = adapterData.name
            //binding.tvType.text = adapterData.type

            if (adapterData.value == "1") {
                binding.img.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorRed),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )

//                binding.llMain.background = ContextCompat.getDrawable(context, R.drawable.shape_round_menu_tap)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.tvCurrent.setTextColor(ContextCompat.getColor(context, R.color.black))

                val animation: Animation = AlphaAnimation(1f, 0f)
                animation.setDuration(600)
                animation.interpolator = LinearInterpolator()
                animation.setRepeatCount(Animation.INFINITE)
                animation.repeatMode = Animation.REVERSE
                binding.llMain.startAnimation(animation)
            } else {
                binding.img.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorGreen),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
//                binding.llMain.background = ContextCompat.getDrawable(context, R.drawable.shape_round_menu)
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvCurrent.setTextColor(ContextCompat.getColor(context, R.color.white))

            }

            val bgColor = getColorForPosition(position, context)
            binding.llMain.background = getRoundedBackground(bgColor)

            binding.tvCurrent.apply {
                text = if (adapterData.value == "1") {
//                    setBackgroundResource(R.drawable.bg_red)
//                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    "EMPTY"
                } else {
//                    setBackgroundResource(R.drawable.bg_green)
//                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    "FULL"
                }
            }


        }

        fun getColorForPosition(position: Int, context: Context): Int {
            // List of your colors (make sure these are defined in colors.xml)
            val colors = listOf(
                R.color.oxygen,
                R.color.nitrogen,
                R.color.co2,
                R.color.vacuum,
                R.color.normal_air,
                R.color.air_bar_3,
                R.color.air_bar_7
            )
            return ContextCompat.getColor(context, colors[position % colors.size])
        }

        fun getRoundedBackground(color: Int): Drawable {
            return GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                this.cornerRadius = 10f
                setColor(color)
            }
        }
    }


}