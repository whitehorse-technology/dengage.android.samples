package com.iqonic.shophop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.iqonic.shophop.R
import com.iqonic.shophop.utils.extensions.loadImageFromUrl
import kotlinx.android.synthetic.main.item_slider.view.imgSlider

class HomeSliderAdapter(private var mImg: ArrayList<String>) : PagerAdapter() {
    var size: Int? = null

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)

        view.imgSlider.loadImageFromUrl(mImg[position])

        parent.addView(view)
        return view
    }

    override fun isViewFromObject(v: View, `object`: Any): Boolean = v === `object` as View

    override fun getCount(): Int = mImg.size

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) = parent.removeView(`object` as View)

}
