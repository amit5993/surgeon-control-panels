package com.surgeon.controlpanels.activity

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.adapter.SettingMenuAdapter
import com.surgeon.controlpanels.adapter.SettingMenuAdapter.ClickSettingMenuInterface
import com.surgeon.controlpanels.databinding.ActivitySettingBinding
import com.surgeon.controlpanels.fragment.ColorFragment
import com.surgeon.controlpanels.fragment.OtherFragment
import com.surgeon.controlpanels.model.ColorThemeModel
import com.surgeon.controlpanels.model.SettingMenuModel


class Setting : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    lateinit var activity: Activity
    var menuList: ArrayList<SettingMenuModel> = ArrayList()

    companion object {
        lateinit var fragmentActive: Fragment
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activity = this


        binding.imgClose.setOnClickListener {
            finish()
        }

        setMenuAdapter()

        setFragment()


    }

    private fun setMenuAdapter() {
        menuList.add(SettingMenuModel("Devices", true))
        menuList.add(SettingMenuModel("Color", false))
//        menuList.add(SettingMenuModel("Other", false))

        binding.tvTitle.text = menuList[0].name

        val menuAdapter = SettingMenuAdapter(activity, menuList)
        binding.rvSettingMenu.adapter = menuAdapter
        menuAdapter.registerInterface(object : ClickSettingMenuInterface {
            override fun clickSettingMenu(dataS: SettingMenuModel, position: Int) {

                for (i in menuList.indices) {
                    menuList[i].isSelected = false
                }

                menuList[position].isSelected = true
                binding.tvTitle.text = menuList[position].name

                clickMenuList(menuList[position])

                menuAdapter.notifyDataSetChanged()

//                val menuAdapter = SettingMenuAdapter(activity, menuList)
//                rvSettingMenu.adapter = menuAdapter

            }
        })
    }

    private fun clickMenuList(d: SettingMenuModel) {

        when (d.name) {
            "TV" -> {
            }

            "Color" -> {
                fragmentActive = ColorFragment()
            }

            "Other" -> {
                fragmentActive = OtherFragment()
            }
        }

        setFragment()

    }

    fun setFragment() {


//            val args = Bundle()
//            val personString = Gson().toJson(foodData)
//            args.putString(Constant.ARRAY_CHECK_OUT, personString)
//            fragmentActive.arguments = args

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragmentActive)
        transaction.commit()

    }

    fun setFragment(dataS: ColorThemeModel) {

        val args = Bundle()
        val personString = Gson().toJson(dataS)
        args.putString("COLOR_THEME", personString)
        fragmentActive.arguments = args

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragmentActive)
        transaction.commit()

    }

}