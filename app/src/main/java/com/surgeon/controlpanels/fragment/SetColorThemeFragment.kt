package com.surgeon.controlpanels.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.activity.Setting
import com.surgeon.controlpanels.common.getGradient
import com.surgeon.controlpanels.common.showCustomToastLayout
import com.surgeon.controlpanels.databinding.FragmentSetColorThemeBinding
import com.surgeon.controlpanels.db.DbHelper
import com.surgeon.controlpanels.model.ColorThemeModel
import com.surgeon.controlpanels.model.theme.ThemeModel
import com.google.gson.Gson
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.lang.String


class SetColorThemeFragment : Fragment(), View.OnClickListener {


    private lateinit var binding: FragmentSetColorThemeBinding
    private lateinit var activity: Activity
    private var color1 = ""
    private var color2 = ""
    private lateinit var dbHelper: DbHelper
    private lateinit var colorThemeModel: ColorThemeModel
    private var themeDataDb: ThemeModel = ThemeModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetColorThemeBinding.inflate(inflater, container, false)
        activity = requireActivity()
        dbHelper = DbHelper(activity)

        init()
        initAction()

        return binding.root
    }

    private fun init() {

        val arguments = arguments
        val checkout = arguments!!.getString("COLOR_THEME")
        Log.e("SetColorThemeFragment", checkout.toString())
        colorThemeModel = Gson().fromJson(checkout, ColorThemeModel::class.java)

        binding.tvTitle.text = colorThemeModel.menuName

        themeDataDb = dbHelper.getTheme(colorThemeModel.type)

        if(themeDataDb.id == 0){
            showCustomToastLayout(activity, "Something want wrong. Please try after some time...!")
            openColorFragment()
        }

        try {
            color1 = themeDataDb.color1
            binding.ed1.setText(themeDataDb.color1)
            binding.view1.setBackgroundColor(Color.parseColor(themeDataDb.color1))

            color2 = themeDataDb.color2
            binding.ed2.setText(themeDataDb.color2)
            binding.view2.setBackgroundColor(Color.parseColor(themeDataDb.color2))
            binding.viewOutput.setBackgroundDrawable(
                getGradient(
                    themeDataDb.color1,
                    themeDataDb.color2
                )
            )
        } catch (e: Exception) {
            println()
        }

    }

    private fun initAction() {

        binding.cv1.setOnClickListener(this)
        binding.cv2.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cv1 -> {
                openColorPickerDialogue(1, Color.parseColor(themeDataDb.color1))
            }

            R.id.cv2 -> {
                openColorPickerDialogue(2, Color.parseColor(themeDataDb.color2))
            }

            R.id.btnSave -> {

                val t = ThemeModel()
                t.color1 = binding.ed1.text.toString().trim()
                t.color2 = binding.ed2.text.toString().trim()
                t.type = themeDataDb.type
                t.fontColor = themeDataDb.fontColor

                dbHelper.updateTheme(t, themeDataDb.id)

                openColorFragment()
            }

            R.id.btnCancel -> {
                openColorFragment()
            }
        }
    }

    private fun openColorFragment() {
        Setting.fragmentActive = ColorFragment()
        (activity as Setting?)!!.setFragment()
    }


    private fun openColorPickerDialogue(i: Int, c: Int) {

        val colorPickerDialogue = AmbilWarnaDialog(
            activity, c,
            object : OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog) {

                }

                @SuppressLint("SetTextI18n")
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    if (i == 1) {
                        color1 =  String.format("#%06X", (0xFFFFFF and color))
                        binding.view1.setBackgroundColor(Color.parseColor(color1))
                        binding.ed1.setText(color1)
                    } else {
                        color2 = String.format("#%06X", (0xFFFFFF and color))
                        binding.view2.setBackgroundColor(Color.parseColor(color2))
                        binding.ed2.setText(color2)
                    }
                    binding.viewOutput.setBackgroundDrawable(getGradient(color1, color2))



                }
            })
        colorPickerDialogue.show()
    }


}