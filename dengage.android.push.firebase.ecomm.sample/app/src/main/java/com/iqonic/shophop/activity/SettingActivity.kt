package com.iqonic.shophop.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.adapter.LanguageAdapter
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View

    private val mCartCountChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setCartCount()
        }

    }
    private var mLanguageName = arrayOf("English (US)", "English (Canada)")
    private var mCountryImg = intArrayOf(R.drawable.ic_usflag, R.drawable.ic_canadaflag)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        title = getString(R.string.title_setting)
        setToolbar(toolbar)
        registerCartCountChangeReceiver(mCartCountChangeReceiver)
        val adapter = LanguageAdapter(this, mCountryImg, mLanguageName)
        sLanguage.adapter = adapter

        if (isLoggedIn()) {
            tvEmail.text = getEmail()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        val menuWishItem: MenuItem = menu!!.findItem(R.id.action_cart)
        val menuSearch: MenuItem = menu.findItem(R.id.action_search)
        menuWishItem.isVisible = true
        menuSearch.isVisible = false
        mMenuCart = menuWishItem.actionView
        mMenuCart.ivCart.setColorFilter(this.color(R.color.textColorPrimary))
        menuWishItem.actionView.onClick {
            launchActivity<MyCartActivity> { }
        }
        setCartCount()
        return super.onCreateOptionsMenu(menu)
    }

    fun setCartCount() {
        val count = getCartCount()
        mMenuCart.tvNotificationCount.text =count
        if (count.checkIsEmpty()|| count=="0") {
            mMenuCart.tvNotificationCount.hide()
        } else {
            mMenuCart.tvNotificationCount.show()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(mCartCountChangeReceiver)
        super.onDestroy()
    }
}


