package com.iqonic.shophop.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.fragments.*
import com.iqonic.shophop.models.CategoryData
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.Constants.AppBroadcasts.CARTITEM_UPDATE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.ORDER_COUNT_CHANGE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.PROFILE_UPDATE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.WISHLIST_UPDATE
import com.iqonic.shophop.utils.Constants.SharedPref.KEY_ORDER_COUNT
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.item_navigation_category.view.*
import kotlinx.android.synthetic.main.layout_sidebar.*
import kotlinx.android.synthetic.main.menu_cart.*
import kotlinx.android.synthetic.main.toolbar.*

class DashBoardActivity : AppBaseActivity() {

    private var count: String = ""
    private val mHomeFragment = HomeFragment()
    private val mWishListFragment = WishListFragment()
    private val mCartFragment = MyCartFragment()
    private val mProfileFragment = ProfileFragment()
    private val mViewAllProductFragment = ViewAllProductFragment()
    private val mSearchFragment = SearchFragment()
    private var selectedFragment: Fragment? = null

    private val mCartItemChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                CART_COUNT_CHANGE -> setCartCountFromPref()
                ORDER_COUNT_CHANGE -> setOrderCount()
                PROFILE_UPDATE -> setUserInfo()
                WISHLIST_UPDATE -> setWishCount()
                CARTITEM_UPDATE -> {
                    mCartFragment.setCart()
                }
            }
        }
    }

    private fun setOrderCount() {
        tvOrderCount.text =
                getSharedPrefInstance().getIntValue(KEY_ORDER_COUNT, 0).toDecimalFormat()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        registerCartCountChangeReceiver(mCartItemChangedReceiver)
        registerOrderCountChangeReceiver(mCartItemChangedReceiver)
        registerProfileUpdateReceiver(mCartItemChangedReceiver)
        registerWishListReceiver(mCartItemChangedReceiver)
        registerCartReceiver(mCartItemChangedReceiver)

        setToolbar(toolbar)
        setUpDrawerToggle()
        setListener()

        if (isLoggedIn()) {
            cartCount()
            setOrderCount()
            setWishCount()
            setCartCountFromPref()
            llInfo.show()

        }

        ivCloseDrawer.onClick {
            closeDrawer()
        }


        setUserInfo()
        tvVersionCode.text = "v " + getAppVersionName()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCode.ACCOUNT) {
            loadWishlistFragment()
        }
    }


    private fun setUserInfo() {
        txtDisplayName.text = getUserFullName()
        if (getProfile().isNotEmpty() && isLoggedIn()) {
            val uri = Uri.parse(getProfile())
            if (uri != null) civProfile.setImageURI(uri)
        }
    }

    private fun setWishCount() {
        tvWishListCount.text =
                getSharedPrefInstance().getIntValue(Constants.SharedPref.KEY_WISHLIST_COUNT, 0).toDecimalFormat()
        mWishListFragment.wishListItemChange()
    }


    private fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(llLeftDrawer))
            drawerLayout.closeDrawer(llLeftDrawer)
    }

    private fun setUpDrawerToggle() {
        val toggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                main.translationX = slideOffset * drawerView.width
                (drawerLayout).bringChildToFront(drawerView)
                (drawerLayout).requestLayout()
            }
        }
        toggle.setToolbarNavigationClickListener {
            if (!mSearchFragment.isVisible) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
        toggle.isDrawerIndicatorEnabled = false
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_drawer, theme)
        toggle.setHomeAsUpIndicator(drawable)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setListener() {
        loadHomeFragment()
        civProfile.onClick {
            if (isLoggedIn()) {
                launchActivity<EditProfileActivity>()
            } else {
                launchActivity<SignInUpActivity>()
            }
        }
        llHome.onClick {
            closeDrawer()
            enable(ivHome)
            loadFragment(mHomeFragment)
            title = getString(R.string.home)
        }

        llWishList.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>()
                return@onClick
            }
            closeDrawer()
            enable(ivWishList)
            loadFragment(mWishListFragment)
            title = getString(R.string.lbl_wish_list)
        }
        llWishlistData.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>()
                return@onClick
            }
            closeDrawer()
            loadWishlistFragment()
        }

        llCart.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>()
                return@onClick
            }
            closeDrawer()
            enable(ivCart)
            tvNotificationCount.hide()
            loadFragment(mCartFragment)
            title = getString(R.string.cart)
        }

        llProfile.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>()
                return@onClick
            }
            closeDrawer()
            enable(ivProfile)
            loadFragment(mProfileFragment)
            title = getString(R.string.profile)

        }

        tvAccount.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignInUpActivity>()
            } else {
                launchActivity<AccountActivity>(Constants.RequestCode.ACCOUNT)
            }
            closeDrawer()

        }
        llReward.onClick {
            if (isLoggedIn()) {
                launchActivity<RewardActivity>()
            } else {
                launchActivity<SignInUpActivity>()
            }
            closeDrawer()

        }
        tvHelp.onClick {
            launchActivity<HelpActivity>()
            closeDrawer()

        }
        tvSetting.onClick {
            launchActivity<SettingActivity>()
            closeDrawer()

        }
        tvFaq.onClick {
            launchActivity<FAQActivity>()
            closeDrawer()
        }
        tvContactus.onClick {
            launchActivity<ContactUsActivity>()
            closeDrawer()
        }
        ivCloseDrawer.onClick {
            closeDrawer()
        }
        llOrders.onClick {
            if (isLoggedIn()) {
                launchActivity<OrderActivity>()
            } else {
                launchActivity<SignInUpActivity>()
            }
            closeDrawer()
        }
    }

    private fun showCartCount() {
        if (isLoggedIn() && !count.checkIsEmpty() && !count.equals("0", false)) {
            tvNotificationCount.show()
        } else {
            tvNotificationCount.hide()
        }

    }

    private fun loadFragment(aFragment: Fragment) {
        when {
            mSearchFragment.isVisible -> {
                removeFragment(mSearchFragment)
            }
        }
        if (selectedFragment != null) {
            if (selectedFragment == aFragment) {
                return
            }
            hideFragment(selectedFragment!!)
        }
        if (aFragment.isAdded) {
            showFragment(aFragment)
        } else {
            addFragment(aFragment, R.id.container)
        }
        selectedFragment = aFragment
    }

    fun loadHomeFragment() {
        enable(ivHome)
        loadFragment(mHomeFragment)
        title = getString(R.string.home)
    }

    private fun loadWishlistFragment() {
        enable(ivWishList)
        loadFragment(mWishListFragment)
        title = getString(R.string.lbl_wish_list)
    }

    private fun enable(aImageView: ImageView?) {
        disableAll()
        showCartCount()
        aImageView?.background = getDrawable(R.drawable.bg_circle_primary_light)
        aImageView?.applyColorFilter(color(R.color.colorPrimary))
    }

    private fun disableAll() {
        disable(ivHome)
        disable(ivWishList)
        disable(ivCart)
        disable(ivProfile)
    }

    private fun disable(aImageView: ImageView?) {
        aImageView?.background = null
        aImageView?.applyColorFilter(color(R.color.textColorSecondary))
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            mViewAllProductFragment.isVisible -> loadHomeFragment()
            mSearchFragment.isVisible -> {
                removeFragment(mSearchFragment)
                loadHomeFragment()
            }
            !mHomeFragment.isVisible -> loadHomeFragment()
            else -> super.onBackPressed()
        }
    }


    private fun cartCount() {
        setCartCountFromPref()
        sendCartCountChangeBroadcast()
    }

    private fun setCartCountFromPref() {
        count = getCartCount()

        tvNotificationCount.text = count
        showCartCount()
        if (mCartFragment.isVisible) {
            tvNotificationCount.hide()
        }
    }


    override fun onDestroy() {
        unregisterReceiver(mCartItemChangedReceiver)
        super.onDestroy()
    }

    private var mDrawables = intArrayOf(
            R.drawable.ic_men,
            R.drawable.ic_dress,
            R.drawable.ic_dress_kids,
            R.drawable.ic_bag,
            R.drawable.ic_watches,
            R.drawable.ic_candle,
            R.drawable.ic_glasses,
            R.drawable.ic_footwear,
            R.drawable.ic_sports
    )


    fun setDrawerCategory(it: ArrayList<CategoryData>) {
        rvCategory.create(it.size, R.layout.item_navigation_category, it, getVerticalLayout(false), onBindView = { item, position ->
            tvCategory.text = item.name
            ivCat.setImageResource(mDrawables[position])
        }, itemClick = { item, _ ->
            launchActivity<SubCategoryActivity> {
                putExtra(Constants.KeyIntent.DATA, item)
            }
        })
        rvCategory.isNestedScrollingEnabled = false
    }

}
