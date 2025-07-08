package com.surgeon.controlpanels.common

import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.model.CallModel
import com.surgeon.controlpanels.model.ColorThemeModel
import com.surgeon.controlpanels.model.MGPSData
import com.surgeon.controlpanels.model.MenuModel
import com.surgeon.controlpanels.model.TVData
import com.surgeon.controlpanels.model.TVListModel

class AllList {

    companion object {

        fun initMenuList(): ArrayList<ArrayList<MenuModel>> {
            val menuList: ArrayList<ArrayList<MenuModel>> = ArrayList()

            var tempList: ArrayList<MenuModel> = ArrayList()
            tempList.add(MenuModel(0, "Temp", "0.0${Constant.degreeSymbol}", Constant.temp))
            tempList.add(MenuModel(R.drawable.ic_timer, "Timer", "", Constant.timer))
            tempList.add(MenuModel(0, "Rh", "00", Constant.hd))
            tempList.add(MenuModel(R.drawable.ic_music, "Music", "", Constant.music))
            tempList.add(MenuModel(R.drawable.ic_light, "Lighting", "", Constant.light))
            tempList.add(MenuModel(R.drawable.ic_gas, "MGPS", "", Constant.mgps))
            //tempList.add(MenuModel(R.drawable.ic_pacs, "Entrance", "", Constant.entrance))

            menuList.add(tempList)

            return menuList

        }

        fun initCallList(): ArrayList<CallModel> {

            val callList: ArrayList<CallModel> = ArrayList()
            callList.add(CallModel("Reception", 12))
            callList.add(CallModel("Accounts", 13))
            callList.add(CallModel("HR", 14))
            callList.add(CallModel("Sales", 15))
            callList.add(CallModel("Blood Room", 16))
            callList.add(CallModel("X-ray", 17))

            return callList

        }

        fun initTVList(): ArrayList<TVListModel> {
            val list: ArrayList<TVListModel> = ArrayList()
            list.add(TVListModel("350925374280745", "TS5LWYA"))
            list.add(TVListModel("350925374492035", "2C53AKU"))
            list.add(TVListModel("350925375318304", "OWMVLFE"))
            list.add(TVListModel("350925379758042", "JGRBMB6"))
            list.add(TVListModel("350925373175813", "2OJ424T"))
            list.add(TVListModel("350925378276269", "VPCGHVL"))
            list.add(TVListModel("350925378129906", "6CE6YKN"))
            return list
        }


        fun initColorThemeList(): ArrayList<ColorThemeModel> {

            val list: ArrayList<ColorThemeModel> = ArrayList()
            list.add(ColorThemeModel("Main", Constant.main))
            list.add(ColorThemeModel("Temp", Constant.temp))
            list.add(ColorThemeModel("Timer", Constant.timer))
            list.add(ColorThemeModel("Humidity", Constant.hd))
            list.add(ColorThemeModel("Lighting", Constant.light))
//            list.add(ColorThemeModel("Call", Constant.call))
//            list.add(ColorThemeModel("Alert", Constant.alert))
            list.add(ColorThemeModel("MGPS", Constant.mgps))
//            list.add(ColorThemeModel("CCTV", Constant.cctv))

            return list

        }

        fun initTVForCCTVList(): ArrayList<TVData> {
            val list: ArrayList<TVData> = ArrayList()
            list.add(TVData("TV 1", "Playing TV 1", false))
            list.add(TVData("TV 2", "Playing TV 2", false))
            return list
        }

        fun initTMGPSList(): ArrayList<MGPSData> {
            val list: ArrayList<MGPSData> = ArrayList()
            list.add(MGPSData("Oxygen",  true))
            list.add(MGPSData("Nitrogen",  false))
            list.add(MGPSData("CO2",  false))
            list.add(MGPSData("Vacuum",  false))
            list.add(MGPSData("Normal Air",  false))
            list.add(MGPSData("Air 3 Bar",  false))
            list.add(MGPSData("Air 7 Bar",  false))
            return list
        }


    }
}