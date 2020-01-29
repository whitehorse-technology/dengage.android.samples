package com.iqonic.shophop.fragments

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.activity.DashBoardActivity
import com.iqonic.shophop.activity.SearchActivity
import com.iqonic.shophop.activity.SubCategoryActivity
import com.iqonic.shophop.activity.ViewAllProductActivity
import com.iqonic.shophop.adapter.HomeSliderAdapter
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemCategoryBinding
import com.iqonic.shophop.databinding.ItemHomeNewestProductBinding
import com.iqonic.shophop.models.*
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {
    private var mFeatured: ArrayList<ProductModel> = ArrayList()
    private var imgLayoutParams: LinearLayout.LayoutParams? = null
    private var mRecentProductAdapter: BaseRecyclerAdapter<ProductModel, ItemHomeNewestProductBinding>? =
            null
    private var mPopularProductsAdapter: BaseRecyclerAdapter<ProductModel, ItemHomeNewestProductBinding>? =
            null
    private var mNewestProductAdapter: BaseRecyclerAdapter<ProductModel, ItemHomeNewestProductBinding>? =
            null
    private lateinit var mProductModel: ProductModel
    private val mImg = ArrayList<String>()
    var image: String = ""
    var size: Int? = 5
    private var mCategoryAdapter: BaseRecyclerAdapter<CategoryData, ItemCategoryBinding>? = null
    private var mColorArray = intArrayOf(
            R.color.cat_1,
            R.color.cat_2,
            R.color.cat_3,
            R.color.cat_4,
            R.color.cat_5
    )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        imgLayoutParams = activity?.productLayoutParams()


        setRecyclerView(rcvRecentSearch)
        setRecyclerView(rcvNewestProduct)
        setRecyclerView(rcvFeaturedProducts)
        setRecyclerView(rcvCategory)

        mRecentProductAdapter = getNewestProductAdapter()
        mPopularProductsAdapter = getFeaturedAdapter()
        mNewestProductAdapter = getNewestProductAdapter()
        mCategoryAdapter = getCategoryAdapter()

        rcvRecentSearch.adapter = mRecentProductAdapter
        rcvNewestProduct.adapter = mNewestProductAdapter
        rcvFeaturedProducts.adapter = mPopularProductsAdapter
        rcvCategory.adapter = mCategoryAdapter
        mRecentProductAdapter?.setModelSize(5)
        mPopularProductsAdapter?.setModelSize(5)
        setClickEventListener()
        listAllProducts()
        listAllProductCategories()
    }

    override fun onResume() {
        super.onResume()
        mRecentProductAdapter?.addItems(getRecentItems())


    }
    private fun getCategoryAdapter(): BaseRecyclerAdapter<CategoryData, ItemCategoryBinding>? {
        return object : BaseRecyclerAdapter<CategoryData, ItemCategoryBinding>() {
            override val layoutResId: Int = R.layout.item_category

            override fun onBindData(model: CategoryData, position: Int, dataBinding: ItemCategoryBinding) {
                dataBinding.ivCategory.setImageResource(mDrawables[position])
                dataBinding.ivCategory.changeBackgroundTint(
                        ContextCompat.getColor(
                                context!!,
                                mColorArray[position % mColorArray.size]
                        )
                )
                dataBinding.tvCatName.setTextColor(activity!!.color(mColorArray[position % mColorArray.size]))
            }

            override fun onItemClick(view: View, model: CategoryData, position: Int, dataBinding: ItemCategoryBinding) {
                activity?.launchActivity<SubCategoryActivity> {
                    putExtra(Constants.KeyIntent.DATA, model)
                }
            }

            override fun onItemLongClick(view: View, model: CategoryData, position: Int) {

            }

        }
    }

    private fun getRecentItems(): ArrayList<ProductModel> {
        val list = recentProduct()
        list.reverse()
        if (list.size <= 0) {
            rlRecentSearch.hide()
        } else {
            if (list.size<=5){
                viewRecentSearch.hide()
            }else{
                viewRecentSearch.show()
            }
            rlRecentSearch.show()
        }
        return list
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    private fun listAllProducts() {
        listAllProducts {
            mNewestProductAdapter?.setModelSize(5)
            mNewestProductAdapter?.addItems(it)

            it.forEach { productModel ->
                if (productModel.featured) {
                    mFeatured.add(productModel)
                }
            }
            if (mFeatured.size == 0) {
                rlFeatured.hide()
                rcvFeaturedProducts.hide()
            } else {
                mPopularProductsAdapter?.addItems(mFeatured)
            }
            mNewestProductAdapter?.setModelSize(5)
            mPopularProductsAdapter?.setModelSize(5)

            for (i in 1 until 7) {
                mImg.add("/banners/b$i.jpg")
            }


            val adapter = HomeSliderAdapter(mImg)
            homeSlider.adapter = adapter
            dots.attachViewPager(homeSlider)
            dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)

        }
    }


    private fun setClickEventListener() {
        viewRecentSearch.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(Constants.KeyIntent.TITLE, "Recent Search")
                putExtra(Constants.KeyIntent.VIEWALLID, Constants.viewAllCode.RECENTSEARCH)
            }
        }

        viewPopular.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(Constants.KeyIntent.TITLE, "Featured Products")
                putExtra(Constants.KeyIntent.VIEWALLID, Constants.viewAllCode.FEATURED)
            }
        }

        viewNewest.onClick {
            activity?.launchActivity<ViewAllProductActivity> {
                putExtra(Constants.KeyIntent.TITLE, "Newest Products")
                putExtra(Constants.KeyIntent.VIEWALLID, Constants.viewAllCode.NEWEST)
            }
        }
    }

    private fun setRecyclerView(recyclerView: RecyclerView?) {
        recyclerView?.setHorizontalLayout()
    }

    private fun getNewestProductAdapter(): BaseRecyclerAdapter<ProductModel, ItemHomeNewestProductBinding> {
        return object : BaseRecyclerAdapter<ProductModel, ItemHomeNewestProductBinding>() {
            override fun onItemClick(view: View, model: ProductModel, position: Int, dataBinding: ItemHomeNewestProductBinding) {
                (activity as AppBaseActivity).showProductDetail(model)
               // mRecentProductAdapter?.addItems(getRecentItems())
               // rlRecentSearch.show()
            }

            override val layoutResId: Int = R.layout.item_home_newest_product

            override fun onBindData(model: ProductModel, position: Int, dataBinding: ItemHomeNewestProductBinding) {
                if (model.images.isNotEmpty()) {
                    dataBinding.ivProduct.loadImageFromUrl(model.images[0].src)
                }
                dataBinding.ivProduct.layoutParams = imgLayoutParams
                if (model.on_sale) {
                    dataBinding.tvDiscountPrice.text = model.sale_price.currencyFormat()

                } else {
                    dataBinding.tvDiscountPrice.text = model.price.currencyFormat()
                }
                dataBinding.tvOriginalPrice.applyStrike()
                dataBinding.tvOriginalPrice.text = model.regular_price.currencyFormat()
            }

            override fun onItemLongClick(view: View, model: ProductModel, position: Int) {

            }
        }
    }

    private fun getFeaturedAdapter(): BaseRecyclerAdapter<ProductModel, ItemHomeNewestProductBinding> {
        return object : BaseRecyclerAdapter<ProductModel, ItemHomeNewestProductBinding>() {
            override fun onItemClick(view: View, model: ProductModel, position: Int, dataBinding: ItemHomeNewestProductBinding) {
                (activity as AppBaseActivity).showProductDetail(model)
               // mRecentProductAdapter?.addItems(getRecentItems())
                //rlRecentSearch.show()
            }

            override val layoutResId: Int = R.layout.item_home_newest_product

            override fun onBindData(model: ProductModel, position: Int, dataBinding: ItemHomeNewestProductBinding) {
                mProductModel = model
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_dashboard, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                activity?.launchActivity<SearchActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun listAllProductCategories() {
        categoryFromAssets(onApiSuccess = {
            mCategoryAdapter?.addItems(it)
            rcvCategory.adapter = mCategoryAdapter
            (activity as DashBoardActivity).setDrawerCategory(it)
        })

    }
}