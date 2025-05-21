package com.surgeon.controlpanels.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.surgeon.controlpanels.activity.Setting
import com.surgeon.controlpanels.adapter.ColorThemeAdapter
import com.surgeon.controlpanels.common.AllList
import com.surgeon.controlpanels.databinding.FragmentColorBinding
import com.surgeon.controlpanels.model.ColorThemeModel

class ColorFragment : Fragment() {


    private lateinit var binding: FragmentColorBinding
    private lateinit var activity: Activity

    private var list: ArrayList<ColorThemeModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorBinding.inflate(inflater, container, false)
        activity = requireActivity()

        setAdapter()

        return binding.root
    }

    private fun setAdapter() {
        list = AllList.initColorThemeList()

        val adapter = ColorThemeAdapter(activity, list)
        adapter.registerInterface(object : ColorThemeAdapter.ClickColorThemeInterface {
            override fun clickColorTheme(dataS: ColorThemeModel, position: Int) {
                Setting.fragmentActive = SetColorThemeFragment()
                (activity as Setting?)!!.setFragment(dataS)

            }

        })
        binding.rvColor.adapter = adapter

    }


}