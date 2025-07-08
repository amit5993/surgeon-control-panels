package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.surgeon.controlpanels.databinding.RawLightBinding
import com.surgeon.controlpanels.model.dbsokect.LightDataModel
import com.google.gson.Gson


class LightAdapter(
    private val context: Activity, private val AdapterList: List<LightDataModel>
) : RecyclerView.Adapter<LightAdapter.ViewHolder>() {
    private var clickList: ClickLightInterface? = null

    interface ClickLightInterface {
//        fun clickLight(
//            dataS: ParameterData, position: Int
//        )

        fun clickLightOnOff(dataS: LightDataModel, position: Int, isChecked: Boolean)
        fun updateIntensity(dataS: LightDataModel, position: Int, progress: Int)
    }

    fun registerInterface(c: ClickLightInterface?) {
        this.clickList = c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RawLightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return AdapterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adapterData = AdapterList[position]
        Log.e("light_data", Gson().toJson(adapterData))
        holder.bind(adapterData, position)

//        holder.itemView.setOnClickListener {
//            if (clickList != null) {
//                clickList!!.clickLight(adapterData, position)
//            }
//        }
    }

    inner class ViewHolder(private val binding: RawLightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(adapterData: LightDataModel, position: Int) {
            binding.tvLight.text = adapterData.name

            // Set initial state without triggering the listener
            binding.swOnOffLight.setOnCheckedChangeListener(null)
            binding.swOnOffLight.isChecked = adapterData.onOffValue == "1"
            binding.llLight.visibility =
                if (adapterData.onOffValue == "1") View.VISIBLE else View.GONE

            binding.sbLight.progress = adapterData.intensityValue
            binding.tvProgress.text = "${adapterData.intensityValue}%"
            binding.sbLight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                private var userSelectedValue: Int = 0

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val roundedProgress = (progress / 5) * 5
                    seekBar?.progress = roundedProgress
                    Log.d("SeekBar", "Progress: $roundedProgress")
                    binding.tvProgress.text = "$roundedProgress%"
                    //adapterData.intensityProgress = roundedProgress
                    if (fromUser) {
                        userSelectedValue = roundedProgress
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // Optional: Handle initial touch if needed
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    //showConfirmationDialog(adapterData, position, userSelectedValue)
                    clickList?.updateIntensity(adapterData, position, userSelectedValue)
                    adapterData.intensityValue = userSelectedValue
                }
            })

            // Attach the listener for switch toggle
            binding.swOnOffLight.setOnCheckedChangeListener { _, isChecked ->
                //showConfirmationLightDialog(adapterData, position, isChecked)
                adapterData.onOffValue = if (isChecked) "1" else "0"
                //binding.swOnOffLight.setOnCheckedChangeListener(null)
                binding.swOnOffLight.isChecked = isChecked
                binding.llLight.visibility = if (isChecked) View.VISIBLE else View.GONE

                clickList?.clickLightOnOff(adapterData, position, isChecked)
            }
        }

//        private fun showConfirmationLightDialog(
//            adapterData: LightDataModel, position: Int, isChecked: Boolean
//        ) {
//            AlertDialog.Builder(context)
//                .setTitle("Confirmation")
//                .setMessage(
//                    if (isChecked) "Are you sure you want to turn ON the light?"
//                    else "Are you sure you want to turn OFF the light?"
//                )
//                .setPositiveButton("Yes") { _, _ ->
//                    // Update data and UI when confirmed
//                    adapterData.current_value = if (isChecked) "1" else "0"
//                    binding.swOnOffLight.setOnCheckedChangeListener(null)
//                    binding.swOnOffLight.isChecked = isChecked
//                    binding.llLight.visibility = if (isChecked) View.VISIBLE else View.GONE
//
//                    clickList?.clickLightOnOff(adapterData, position, isChecked)
//                }
//                .setNegativeButton("No") { _, _ ->
//                    // Revert the switch state when canceled
//                    binding.swOnOffLight.setOnCheckedChangeListener(null)
//                    binding.swOnOffLight.isChecked = !isChecked
//                }
//                .setOnDismissListener {
//                    // Reattach listener to ensure further clicks are captured
//                    binding.swOnOffLight.setOnCheckedChangeListener { _, checked ->
//                        showConfirmationLightDialog(adapterData, position, checked)
//                    }
//                }
//                .show()
//        }
//
//        private fun showConfirmationDialog(
//            adapterData: LightDataModel,
//            position: Int,
//            progress: Int
//        ) {
//            AlertDialog.Builder(context)
//                .setTitle("Confirmation")
//                .setMessage("Are you sure you want to set the light to $progress%?")
//                .setPositiveButton("Yes") { _, _ ->
//                    Toast.makeText(context, "Value set to $progress%", Toast.LENGTH_SHORT).show()
//                    clickList?.updateIntensity(adapterData, position, progress)
//                    adapterData.intensityProgress = progress
//                }
//                .setNegativeButton("No") { _, _ ->
//                    // Reset SeekBar if the user cancels
//                    binding.sbLight.progress = adapterData.intensityProgress
//                    binding.tvProgress.text = "${adapterData.intensityProgress}%"
//                    notifyDataSetChanged()
//                }
//                .show()
//        }

    }

}