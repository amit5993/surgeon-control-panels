package com.surgeon.controlpanels.adapter

import android.app.Activity
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.surgeon.controlpanels.R
import com.surgeon.controlpanels.model.MenuModel

class ViewPagerMenuAdapter(
    private val context: Activity,
    list: ArrayList<ArrayList<MenuModel>>
) :
    PagerAdapter(), MenuAdapter.ClickMenuInterface {

    private val viewpagerBannerAdapter: ArrayList<ArrayList<MenuModel>> = list


    private var clickListToPager: ClickMenuInterfaceToPager? = null

    interface ClickMenuInterfaceToPager {
        fun clickMenu(
            foodData: MenuModel?,
            position: Int
        )

    }

    fun registerInterface(clickCategory: ClickMenuInterfaceToPager?) {
        this.clickListToPager = clickCategory
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int {
        return viewpagerBannerAdapter.size
    }

    override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_viewpager_menu, viewGroup, false)

        val rvMenu: RecyclerView = view.findViewById<View>(R.id.rvMenu) as RecyclerView
        rvMenu.itemAnimator = DefaultItemAnimator()
        val viewAdapter: MenuAdapter = MenuAdapter(context, viewpagerBannerAdapter[position])
        viewAdapter.registerInterface(this)
        rvMenu.adapter = viewAdapter
        /**
         * disable Scrolling in recycler View
         * */
//        recyclerViewPlanList.suppressLayout(true)
        viewGroup.addView(view, 0)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }

    override fun clickMenu(data: MenuModel, position: Int) {
        if (clickListToPager != null) {
            clickListToPager!!.clickMenu(data, position)
        }

    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

}