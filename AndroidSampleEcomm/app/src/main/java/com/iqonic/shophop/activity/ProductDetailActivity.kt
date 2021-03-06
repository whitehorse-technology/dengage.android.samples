package com.iqonic.shophop.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.dengage.sdk.DengageEvent
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.adapter.ProductImageAdapter
import com.iqonic.shophop.adapter.TabAdapter
import com.iqonic.shophop.databinding.ActivityProductDetailBinding
import com.iqonic.shophop.fragments.ItemDescriptionFragment
import com.iqonic.shophop.fragments.ItemMoreInfoFragment
import com.iqonic.shophop.fragments.ItemReviewFragment
import com.iqonic.shophop.models.Key
import com.iqonic.shophop.models.ProductModel
import com.iqonic.shophop.models.ProductReviewData
import com.iqonic.shophop.utils.Constants.KeyIntent.DATA
import com.iqonic.shophop.utils.Constants.KeyIntent.IS_ADDED_TO_CART
import com.iqonic.shophop.utils.extensions.*
import com.google.android.material.tabs.TabLayout
import com.segmentify.segmentifyandroidsdk.SegmentifyManager
import com.segmentify.segmentifyandroidsdk.model.PageModel
import com.segmentify.segmentifyandroidsdk.model.RecommendationModel
import com.segmentify.segmentifyandroidsdk.utils.SegmentifyCallback
import kotlinx.android.synthetic.main.activity_product_detail.*



class ProductDetailActivity : AppBaseActivity() {

    private var isAddedTocart: Boolean = false
    private var mFlag: Int = -1
    private lateinit var mMainBinding: ActivityProductDetailBinding
    private lateinit var mProductModel: ProductModel
    private val mImages = ArrayList<String>()
    private lateinit var itemDescription: ItemDescriptionFragment
    private lateinit var itemMoreInfoFragment: ItemMoreInfoFragment
    private lateinit var itemReviewFragment: ItemReviewFragment
    var i: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeTransparentStatusBar()
        if (intent.getSerializableExtra(DATA) == null) {
            toast(R.string.error_something_went_wrong)
            finish()
            return
        }
        // TODO: event product details
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)
        setToolbar(mMainBinding.toolbar)

        mProductModel = intent.getSerializableExtra(DATA) as ProductModel
        mMainBinding.model = mProductModel

        itemDescription = ItemDescriptionFragment.getNewInstance(mProductModel)
        itemMoreInfoFragment = ItemMoreInfoFragment.getNewInstance(mProductModel)
        itemReviewFragment = ItemReviewFragment.getNewInstance(mProductModel)

        listProductReviews()
        if (mProductModel.on_sale)
            tvPrice.text = mProductModel.sale_price.currencyFormat()
        else
            tvPrice.text = mProductModel.price.currencyFormat()
        tvItemProductOriginalPrice.text = mProductModel.regular_price.currencyFormat()

        toolbar_layout.setCollapsedTitleTypeface(fontSemiBold())
        toolbar_layout.setExpandedTitleTypeface(fontSemiBold())
        toolbar_layout.title = mProductModel.name

        //val details = java.util.HashMap<String, Any>()
        //details.put("event_type", "page_view")
        //details.put("page_type", "product")
        //details.put("page_url","")
        //details.put("page_title", mProductModel.name)
        //details.put("product_id", mProductModel.id)
        //details.put("quantity ","")

        //DengageManager.getInstance(applicationContext).sendDeviceEvent("user_events", details)
        //DengageManager.getInstance(applicationContext).sendPageView(details);

        //val model = PageModel()
        //model.category = "Product Page"
        //SegmentifyManager.sendPageView(
        //model,
        //object : SegmentifyCallback<ArrayList<RecommendationModel>> {
        //    override fun onDataLoaded(data: ArrayList<RecommendationModel>) {
        //        data.forEach {
        //            Log.d("Product Page View: ", it.notificationTitle + " "+ it.actionId + " "+ it.errorString + " "+ it.instanceId)
        //        }
        //    }
        //})

        //val pModel = com.segmentify.segmentifyandroidsdk.model.ProductModel()
        //val categories = ArrayList<String>()

        //mProductModel.categories.forEach {
        //    categories.add(it.name)
        //}

        //pModel.productId =  mProductModel.id.toString()
        //pModel.categories = categories
        //pModel.price = mProductModel.price.toDouble()
        //pModel.title = mProductModel.name
        //pModel.image = mProductModel.images.get(0).src
        //pModel.url = mProductModel.external_url

        val colors = ArrayList<String>()
        val sizes = ArrayList<String>()
        val brands = ArrayList<String>()

        mProductModel.attributes.forEach {
            if(it.name=="Color") {
                it.options.forEach { itColor: String ->
                    colors.add(itColor)
                }
            }
            if(it.name=="Size") {
                it.options.forEach { itSize: String ->
                    sizes.add(itSize)
                }
            }
            if(it.name=="Brand") {
                it.options.forEach { itBrand: String ->
                    brands.add(itBrand)
                }
            }
        }

        //pModel.sizes = sizes
        //pModel.colors = colors
        //pModel.brand = brands[0]
        //SegmentifyManager.sendProductView(
            //pModel,
            //object : SegmentifyCallback<ArrayList<RecommendationModel>> {
            //    override fun onDataLoaded(data: ArrayList<RecommendationModel>) {
            //        data.forEach {
            //           Log.d("Product Page Detail: ", it.notificationTitle + " "+ it.actionId + " "+ it.errorString + " "+ it.instanceId)
            //       }
            //   }
            //})

        var data : HashMap<String, Any>
                = HashMap<String, Any> ();
        data.put("page_type", "product");
        data.put("product_id", mProductModel.id.toString());

        DengageEvent.getInstance(applicationContext).pageView(data);

        intHeaderView()
        tvItemProductOriginalPrice.applyStrike()
        setCustomFont()
        if (isLoggedIn()) {
            mFlag = isFavourite(mProductModel.id)
            if (mFlag != -1) {
                changeFavIcon(R.drawable.ic_heart_fill, R.color.favourite_backround, R.color.colorPrimary)
            } else {
                changeFavIcon(R.drawable.ic_heart, R.color.favourite_unselected_background)
            }
        }
        ivFavourite.onClick {
            if (mFlag == -1) {
                if (isLoggedIn()) {
                    addToWishList(mProductModel, onApiSuccess = {
                        mFlag = it
                        changeFavIcon(
                                R.drawable.ic_heart_fill,
                                R.color.favourite_backround,
                                R.color.colorPrimary
                        )
                    })
                }
            } else {
                removeWishList(mFlag, onApiSuccess = {
                    mFlag = -1
                    changeFavIcon(R.drawable.ic_heart, R.color.favourite_unselected_background)
                })
            }
        }
        isAddedTocart = intent.getBooleanExtra(IS_ADDED_TO_CART, false)
        if (!isAddedTocart) btnAddCard.text =
                getString(R.string.lbl_add_to_cart) else btnAddCard.text =
                getString(R.string.lbl_remove_cart)

        btnAddCard.onClick {
            //if (isLoggedIn()) {
                if (isAddedTocart) removeCartItem() else addItemToCart(false)
            //} else launchActivity<SignInUpActivity> { }

        }
        btnBuyNow.onClick {
            if (isLoggedIn()) addItemToCart(true) else launchActivity<SignInUpActivity> { }
        }
        loadBannerAd(R.id.adView)

    }


    private fun intHeaderView() {
        for (i in 0 until mProductModel.images.size) {
            mImages.add(mProductModel.images[i].src)
        }
        val adapter1 = ProductImageAdapter(mImages)
        productViewPager.adapter = adapter1
        dots.attachViewPager(productViewPager)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        val array = arrayOf(itemDescription, itemMoreInfoFragment, itemReviewFragment)
        productTabs!!.addTab(productTabs!!.newTab().setText(getString(R.string.lbl_tabDescription)))
        productTabs!!.addTab(productTabs!!.newTab().setText(getString(R.string.lbl_tab_more_info)))
        productTabs!!.addTab(productTabs!!.newTab().setText(getString(R.string.lbl_tabReviews)))
        val adapter = TabAdapter(supportFragmentManager, array)
        ViewPagerInfo!!.adapter = adapter
        ViewPagerInfo!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(productTabs))
        productTabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                ViewPagerInfo!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        tvItemProductOriginalPrice.applyStrike()
        setCustomFont()
    }

    private fun changeFavIcon(
            drawable: Int,
            backgroundColor: Int,
            iconTint: Int = R.color.textColorSecondary
    ) {
        ivFavourite.setImageResource(drawable)
        ivFavourite.applyColorFilter(color(iconTint))
        ivFavourite.changeBackgroundTint(color(backgroundColor))
    }

    private fun setCustomFont() {
        val vg = productTabs.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount

        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup

            val mTabChildCount = vgTab.childCount

            for (i in 0 until mTabChildCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = fontSemiBold()
                }
            }
        }
    }

    private fun addItemToCart(b: Boolean) {
        var mSelectedColors = ""
        var size = ""
        if (itemDescription.isColorAvailable()) {
            mSelectedColors = itemDescription.getSelectedColors()
            if (mSelectedColors.isEmpty()) {
                snackBar(getString(R.string.msg_selectcolorsize))
                return
            }
        }
        if (itemDescription.isSizeAvailable()) {
            size = itemDescription.getSelectedSize()
            if (size.isEmpty()) {
                snackBar(getString(R.string.msg_selectcolorsize))
                return
            }
        }

        //val details = java.util.HashMap<String, Any>()
        //details.put("event_type", "add_basket")
        // details.put("page_type", "basket")
        //details.put("page_url","")
        //details.put("page_title", mProductModel.name)
        //details.put("product_id", mProductModel.id)
        //details.put("quantity ",1)

        //DengageManager.getInstance(applicationContext).sendDeviceEvent("user_events", details)

        //SegmentifyManager.sendAddOrRemoveBasket("add", mProductModel.id.toString(),1,mProductModel.price.toDouble());


        addCart(getCartObject(mProductModel, mSelectedColors, size))
        btnAddCard.text = getString(R.string.lbl_remove_cart)
        isAddedTocart = true
        if (b) {
            launchActivity<MyCartActivity>()
        }

    }


    private fun removeCartItem() {
        getAlertDialog(getString(R.string.msg_confirmation), onPositiveClick = { _, _ ->
            val key = Key()
            key.product_id = mProductModel.id
            removeItem(key)
            snackBar(getString(R.string.success))
            btnAddCard.text = getString(R.string.lbl_add_to_cart)
            isAddedTocart = false


            //val details = java.util.HashMap<String, Any>()
            //details.put("event_type", "remove_basket")
            //details.put("page_type", "mycart")
            //details.put("page_url","")
            //details.put("page_title", mProductModel.name)
            //details.put("product_id", mProductModel.id)
            //details.put("quantity ","")

            //DengageManager.getInstance(applicationContext).sendDeviceEvent("user_events", details)

            //SegmentifyManager.sendAddOrRemoveBasket("remove",mProductModel.id.toString(),1,mProductModel.price.toDouble());


        }, onNegativeClick = { dialog, _ ->
            dialog.dismiss()
        }).show()
    }

    @SuppressLint("SetTextI18n")
    fun listProductReviews() {
        getProductsReviews(mProductModel.id, onApiSuccess = {
            setReviews(it)
        }, userReviewed = {

        })

    }

    private fun setRating(data: List<ProductReviewData>) {
        var fiveStar = 0
        var fourStar = 0
        var threeStar = 0
        var twoStar = 0
        var oneStar = 0
        for (reviewModel in data) {
            when (reviewModel.rating) {
                5 -> fiveStar++
                4 -> fourStar++
                3 -> threeStar++
                2 -> twoStar++
                1 -> oneStar++
            }
        }
        val mAvgRating = (5 * fiveStar + 4 * fourStar + 3 * threeStar + 2 * twoStar + 1 * oneStar) / (fiveStar + fourStar + threeStar + twoStar + oneStar)
        tvItemProductRating.text = mAvgRating.toString()
    }

    fun setReviews(it: java.util.ArrayList<ProductReviewData>) {
        val mReview: ArrayList<String> = ArrayList()
        it.forEach { review ->
            if (!mReview.contains(review.email)) {
                mReview.add(review.email)
            }
        }
        setRating(it)
        tvItemProductReview.text = String.format("%d Reviewer", mReview.size)
    }


}
