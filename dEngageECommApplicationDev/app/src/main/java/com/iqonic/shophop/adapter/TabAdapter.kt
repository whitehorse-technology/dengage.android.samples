package com.iqonic.shophop.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.iqonic.shophop.fragments.*

class TabAdapter(fm: FragmentManager, val array: Array<BaseFragment>) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return array[position]
    }
    override fun getCount(): Int {
        return array.size
    }
}