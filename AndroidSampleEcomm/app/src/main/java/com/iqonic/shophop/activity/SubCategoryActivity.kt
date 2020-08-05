package com.iqonic.shophop.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.dengage.sdk.DengageEvent
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemProductBinding
import com.iqonic.shophop.fragments.SearchFragment
import com.iqonic.shophop.fragments.ViewAllProductFragment
import com.iqonic.shophop.models.CategoryData
import com.iqonic.shophop.models.ProductModel
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.Constants.KeyIntent.DATA
import com.iqonic.shophop.utils.extensions.*
import com.segmentify.segmentifyandroidsdk.SegmentifyManager
import com.segmentify.segmentifyandroidsdk.model.PageModel
import com.segmentify.segmentifyandroidsdk.model.RecommendationModel
import com.segmentify.segmentifyandroidsdk.utils.SegmentifyCallback
import kotlinx.android.synthetic.main.activity_sub_category.*
import kotlinx.android.synthetic.main.activity_sub_category.viewPopular
import kotlinx.android.synthetic.main.toolbar.*


class SubCategoryActivity : AppBaseActivity() {
    private var imgLayoutParams: LinearLayout.LayoutParams? = null
    lateinit var mCategoryData: CategoryData
    private var mFeatured: ArrayList<ProductModel> = ArrayList()
    private val mImg = ArrayList<String>()
    private var mNewArrivalAdapter = getAdapter()
    private var mPopularAdapter = getFeaturedAdapter()
    private var mViewAllProductFragment = ViewAllProductFragment()
    private var mSearchFragment = SearchFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)
        loadBannerAd(R.id.adView)

        if (intent.getSerializableExtra(DATA) == null) {
            toast(R.string.error_something_went_wrong)
            finish()
            return
        }
        mCategoryData = intent.getSerializableExtra(DATA) as CategoryData

        setToolbar(toolbar)
        title = mCategoryData.name
        imgLayoutParams = productLayoutParams()
        rcvNewArrival.setHorizontalLayout()
        rcvPopular.setHorizontalLayout()
        rcvNewArrival.adapter = mNewArrivalAdapter
        rcvPopular.adapter = mPopularAdapter
        mNewArrivalAdapter.setModelSize(5)
        mPopularAdapter.setModelSize(5)


        //val details = java.util.HashMap<String, Any>()
        //details.put("event_type", "page_view")
        //details.put("page_type", "category")
        //details.put("page_url","")
        //details.put("page_title", title.toString().toLowerCase())
        //details.put("product_id ","")
        //details.put("quantity ", "")

        var data : HashMap<String, Any>
                = HashMap<String, Any> ();
        data.put("page_type", "category");
        data.put("category_id", mCategoryData.id.toString());

        DengageEvent.getInstance(applicationContext).pageView(data);
        //event.sendDeviceEvent("user_events", details)

        val model = PageModel()
        model.category = "Category Page"
        model.subCategory = title.toString().toLowerCase()

        //SegmentifyManager.sendPageView(
        //    model,
        //    object : SegmentifyCallback<ArrayList<RecommendationModel>> {
        //       override fun onDataLoaded(data: ArrayList<RecommendationModel>) {
        //           Log.d("Segmentify: ", data.toString())
        //        }
        //   })




        viewNewArrival.onClick {
            launchActivity<ViewAllProductActivity> {
                putExtra(Constants.KeyIntent.TITLE, getString(R.string.lbl_new_arrival))
                putExtra(Constants.KeyIntent.VIEWALLID, Constants.viewAllCode.CATEGORY_NEWEST)
                putExtra(Constants.KeyIntent.KEYID, mCategoryData.name)

            }
        }
        viewPopular.onClick {
            launchActivity<ViewAllProductActivity> {
                putExtra(Constants.KeyIntent.TITLE, getString(R.string.lbl_popular))
                putExtra(Constants.KeyIntent.VIEWALLID, Constants.viewAllCode.CATEGORY_FEATURED)
                putExtra(Constants.KeyIntent.KEYID, mCategoryData.name)
            }

        }
        getSubCategoryProducts()


    }

    private fun getSubCategoryProducts() {
        subCategoryProducts(mCategoryData.name) {
            mNewArrivalAdapter.addItems(it)
            it.forEach { model ->
                if (model.featured) {
                    mFeatured.add(model)
                }
            }
            if (mFeatured.size == 0) {
                rlFeature.hide()
                rcvPopular.hide()
            } else {
                mPopularAdapter.addItems(mFeatured)
            }

            it.forEach {
                if (it.images.isNotEmpty()) {
                    mImg.add(it.images[0].src)
                }
            }

            if (mImg.size > 0) {
                val handler = Handler()
                val runnable = object : Runnable {
                    var i = 0

                    override fun run() {
                        ivOffer1.loadImageFromUrl(mImg[i])
                        i++
                        if (i > mImg.size - 1) {
                            i = 0
                        }
                        handler.postDelayed(this, 3000)
                    }
                }
                handler.postDelayed(runnable, 3000)
            }


            showProgress(false)
            rlContent.show()
        }
    }



    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu!!.getItem(0)!!.isVisible = !mViewAllProductFragment.isVisible
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            loadSearchFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAdapter(): BaseRecyclerAdapter<ProductModel, ItemProductBinding> {

        return object : BaseRecyclerAdapter<ProductModel, ItemProductBinding>() {
            override fun onItemClick(
                    view: View,
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemProductBinding
            ) {
                if (view.id == R.id.rlItem) {
                    showProductDetail(model)
                }
            }

            override val layoutResId: Int = R.layout.item_product

            override fun onBindData(
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemProductBinding
            ) {
                dataBinding.ivProduct.layoutParams = imgLayoutParams
                dataBinding.tvOriginalPrice.applyStrike()


                if (model.images.isNotEmpty()) {
                    dataBinding.ivProduct.loadImageFromUrl(model.images[0].src)
                }
                if (model.on_sale) {
                    dataBinding.tvDiscountPrice.text = model.sale_price.currencyFormat()

                } else {
                    dataBinding.tvDiscountPrice.text = model.price.currencyFormat()
                }
                dataBinding.tvOriginalPrice.applyStrike()
                dataBinding.tvOriginalPrice.text = model.regular_price.currencyFormat()
                //Glide.with(getAppInstance()).load(model.productImg!!).into(dataBinding.ivProduct)
            }

            override fun onItemLongClick(view: View, model: ProductModel, position: Int) {

            }
        }
    }

    private fun loadSearchFragment() {
        launchActivity<SearchActivity>()
    }

    override fun onBackPressed() {
        when {
            mViewAllProductFragment.isVisible -> {
                title = intent.getStringExtra(Constants.KeyIntent.TITLE)
                removeFragment(mViewAllProductFragment)
                invalidateOptionsMenu()
            }
            mSearchFragment.isVisible -> {
                onBackPressed()
            }
            else -> super.onBackPressed()

        }
    }

    private fun getFeaturedAdapter(): BaseRecyclerAdapter<ProductModel, ItemProductBinding> {
        return object : BaseRecyclerAdapter<ProductModel, ItemProductBinding>() {
            override fun onItemClick(
                    view: View,
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemProductBinding
            ) {
                if (view.id == R.id.rlItem) {
                    showProductDetail(model)
                }
            }

            override val layoutResId: Int = R.layout.item_product

            override fun onBindData(
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemProductBinding
            ) {
                if (model.images.isNotEmpty()) {
                    dataBinding.ivProduct.loadImageFromUrl(model.images[0].src)
                }
                if (model.on_sale)
                    dataBinding.tvDiscountPrice.text = model.sale_price.currencyFormat()
                else
                    dataBinding.tvDiscountPrice.text = model.price.currencyFormat()
                dataBinding.ivProduct.layoutParams = imgLayoutParams
                dataBinding.tvOriginalPrice.applyStrike()
                dataBinding.tvOriginalPrice.text = model.regular_price.currencyFormat()
            }

            override fun onItemLongClick(view: View, model: ProductModel, position: Int) {

            }
        }
    }


}