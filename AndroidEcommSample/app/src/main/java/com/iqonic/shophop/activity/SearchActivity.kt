package com.iqonic.shophop.activity

import android.os.Bundle
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.fragments.SearchFragment
import com.iqonic.shophop.utils.extensions.addFragment

class SearchActivity : AppBaseActivity() {

    private val mSearchFragment = SearchFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search2)
        addFragment(mSearchFragment, R.id.fragmentContainer)
        loadBannerAd(R.id.adView)
    }

    override fun onBackPressed() {
        super.onBackPressed()


    }
}