package com.iqonic.shophop.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.ShopHopApp
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemQuickPayBinding
import com.iqonic.shophop.models.Card
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_quick_pay.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class QuickPayActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View
    private val mCartCountChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setCartCount()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_pay)

        registerCartCountChangeReceiver(mCartCountChangeReceiver)

        setToolbar(toolbar)
        title = getString(R.string.lbl_quick_pay_cards)

        val mCardAdapter = object : BaseRecyclerAdapter<Card, ItemQuickPayBinding>() {
            override fun onItemLongClick(view: View, model: Card, position: Int) {
            }

            override fun onItemClick(view: View, model: Card, position: Int, dataBinding: ItemQuickPayBinding) {
            }

            override val layoutResId: Int = R.layout.item_quick_pay

            override fun onBindData(model: Card, position: Int, dataBinding: ItemQuickPayBinding) {
                dataBinding.model = model
                Glide.with(ShopHopApp.getAppInstance()).load(model.cardImg).into(dataBinding.ivCardbg)
            }
        }
        rvCard.setVerticalLayout()
        rvCard.setHasFixedSize(true)
        mCardAdapter.addItems(getItems())
        rvCard.adapter = mCardAdapter
        btnAddCard.onClick {
            launchActivity<CardActivity> { }
        }
        loadBannerAd(R.id.adView)

    }

    private fun getItems(): ArrayList<Card> {
        val list = ArrayList<Card>()
        val card1 = Card()
        card1.cardImg = R.drawable.card
        card1.cardType = "Debit Card"
        card1.bankName = "NNS Bank"
        card1.cardNumber = "3434  5444  5454  4354"
        card1.validDate = " 12/22"
        card1.holderName = "John"

        val product1 = Card()
        product1.cardImg = R.drawable.card
        product1.cardType = "Debit Card"
        product1.bankName = "MVK Bank"
        product1.cardNumber = "2222 5555 5454 4354"
        product1.validDate = "12/22"
        product1.holderName = "John"

        val product2 = Card()
        product2.cardImg = R.drawable.card
        product2.cardType = "Debit Card"
        product2.bankName = "BDS Bank"
        product2.cardNumber = "5555 5444 0000 4354"
        product2.validDate = "12/22"
        product2.holderName = "Jenny Willams"

        list.add(card1)
        list.add(product1)
        list.add(product2)
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        val menuWishItem: MenuItem = menu!!.findItem(R.id.action_cart)
        val menuSearch: MenuItem = menu.findItem(R.id.action_search)
        menuWishItem.isVisible = true
        menuSearch.isVisible = false
        mMenuCart = menuWishItem.actionView
        mMenuCart.ivCart.setColorFilter(this.color(R.color.textColorPrimary))
        setCartCount()
        menuWishItem.actionView.onClick {
            launchActivity<MyCartActivity> { }
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun setCartCount() {
        val count = getCartCount()
        mMenuCart.tvNotificationCount.text = count
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