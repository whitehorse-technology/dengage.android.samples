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
import com.iqonic.shophop.base.IExpandableListAdapter
import com.iqonic.shophop.databinding.ItemFaqHeadingBinding
import com.iqonic.shophop.databinding.ItemFaqSubheadingBinding
import com.iqonic.shophop.models.Category
import com.iqonic.shophop.models.SubCategory
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_faqactrivity.*
import kotlinx.android.synthetic.main.menu_cart.view.*
import kotlinx.android.synthetic.main.toolbar.*

class FAQActivity : AppBaseActivity() {
    private lateinit var mMenuCart: View
    private lateinit var mFaqAdapter: IExpandableListAdapter<Category, SubCategory, ItemFaqHeadingBinding, ItemFaqSubheadingBinding>

    private val mCartCountChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setCartCount()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faqactrivity)
        title = getString(R.string.title_faq)
        setToolbar(toolbar)
        registerCartCountChangeReceiver(mCartCountChangeReceiver)
        setFaq()
        loadBannerAd(R.id.adView)

    }

    private fun setFaq() {
        mFaqAdapter = object : IExpandableListAdapter<Category, SubCategory, ItemFaqHeadingBinding, ItemFaqSubheadingBinding>(this) {

            override fun bindChildView(view: ItemFaqSubheadingBinding, childObject: SubCategory, groupPosition: Int, childPosition: Int): ItemFaqSubheadingBinding {
                return view
            }

            override fun bindGroupView(view: ItemFaqHeadingBinding, groupObject: Category, groupPosition: Int): ItemFaqHeadingBinding {
                return view
            }

            override val childItemResId: Int = R.layout.item_faq_subheading

            override val groupItemResId: Int = R.layout.item_faq_heading
        }
        exFaq.setAdapter(mFaqAdapter)
        addItems()
    }

    private fun addItems() {
        val mHeading = arrayOf(
                getString(R.string.lbl_account_deactivate),
                getString(R.string.lbl_quick_pay),
                getString(R.string.lbl_return_items),
                getString(R.string.lbl_replace_items)

        )
        val mSubHeading = arrayOf(
                getString(R.string.lbl_account_deactivate),
                getString(R.string.lbl_quick_pay),
                getString(R.string.lbl_return_items),
                getString(R.string.lbl_replace_items)
        )
        val map = HashMap<Category, ArrayList<SubCategory>>()
        val mFaqList = ArrayList<Category>()
        mHeading.forEachIndexed { _: Int, s: String ->
            val heading = Category()
            heading.categoryName = s
            mFaqList.add(heading)
        }
        mFaqList.forEach {
            val list = ArrayList<SubCategory>()
            mSubHeading.forEach {
                val subCat = SubCategory()
                subCat.categoryName = it
                list.add(subCat)
            }
            map[it] = list
        }
        mFaqAdapter.addExpandableItems(mFaqList, map)
//        setExpandableListViewHeight(exFaq, -1)
//        exFaq.setOnGroupClickListener { parent, v, position, id ->
//            setExpandableListViewHeight(parent, position)
//            false
//        }
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
        if (count.checkIsEmpty() || count=="0") {
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
