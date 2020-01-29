package com.iqonic.shophop.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.activity.MyCartActivity
import com.iqonic.shophop.activity.SearchActivity
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemNewestProductBinding
import com.iqonic.shophop.databinding.ItemViewproductgridBinding
import com.iqonic.shophop.models.Attribute
import com.iqonic.shophop.models.Attributes
import com.iqonic.shophop.models.CategoryData
import com.iqonic.shophop.models.ProductModel
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.Constants.viewAllCode.FEATURED
import com.iqonic.shophop.utils.Constants.viewAllCode.NEWEST
import com.iqonic.shophop.utils.HidingScrollListener
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_newest_product.*
import kotlinx.android.synthetic.main.layout_color.view.*
import kotlinx.android.synthetic.main.menu_cart.view.*


class ViewAllProductFragment : BaseFragment() {
    private lateinit var mProductModel: ProductModel

    companion object {
        fun getNewInstance(id: Int, mCategoryId: String?): ViewAllProductFragment {
            val fragment = ViewAllProductFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.KeyIntent.VIEWALLID, id)
            bundle.putSerializable(Constants.KeyIntent.KEYID, mCategoryId)

            fragment.arguments = bundle
            return fragment
        }
    }

    private val mListAdapter = getAdapter()
    private val mGridAdapter = getGridAdapter()
    private var mFlag: Boolean = true
    private var menuCart: View? = null
    private var mId: Int = 0
    private var mCategoryId: String? = ""
    private var filterFragment: FilterFragment? = null
    val listener = object : FilterFragment.FilterListener {
        override fun apply(
                minPrice: Int,
                maxPrice: Int,
                selectedBrands: ArrayList<String>,
                selectedColors: ArrayList<String>,
                selectedSize: ArrayList<String>,
                selectedCategory: ArrayList<String>
        ) {
            val map = HashMap<String, ArrayList<String>>()

            if (selectedBrands.size > 0) {
                map["Brand"] = selectedBrands
            }
            if (selectedSize.size > 0) {
                map["Size"] = selectedSize
            }
            if (selectedColors.size > 0) {
                map["Color"] = selectedColors
            }
            mListAdapter.clearData()
            mGridAdapter.clearData()
            list.forEach {
                if (it.regular_price.toInt() in minPrice..maxPrice && catExist(
                                it,
                                selectedCategory
                        ) && checkIfExist(it, map)
                ) {
                    mListAdapter.addNewItem(it)
                    mGridAdapter.addNewItem(it)
                }
            }
        }

    }

    private fun checkIfExist(
            model: ProductModel,
            map: HashMap<String, ArrayList<String>>
    ): Boolean {
        if (map.size == 0) {
            return true
        }
        map.keys.forEach {
            model.attributes.forEach { attrType ->
                if (attrType.name == it) {
                    map[it]!!.forEach { filterAttr ->
                        if (attrType.options.contains(filterAttr)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun catExist(model: ProductModel, map: ArrayList<String>): Boolean {
        if (map.size == 0) {
            return true
        }
        model.categories.forEach { category ->
            if (map.contains(category.name)) {
                return true
            }
        }
        return false
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_newest_product, container, false)
    }

    var isFeatured = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setClickEventListener()
        rvNewestProduct.adapter = mListAdapter
        rvNewestProduct.rvItemAnimation()
        rvNewestProduct.setOnScrollListener(object : HidingScrollListener(activity) {

            override fun onMoved(distance: Int) {
                rlTop.translationY = -distance.toFloat()
            }

            override fun onShow() {
                rlTop.animate().translationY(0f).setInterpolator(DecelerateInterpolator(2f)).start()
            }

            override fun onHide() {
                rlTop.animate().translationY((-rlTop.height).toFloat())
                        .setInterpolator(AccelerateInterpolator(2f))
                        .start()
            }
        })

        mId = arguments!!.getInt(Constants.KeyIntent.VIEWALLID)
        mCategoryId = arguments!!.getString(Constants.KeyIntent.KEYID)
        when (mId) {
            Constants.viewAllCode.RECENTSEARCH -> {
                mListAdapter.addItems(getRecentItems())
                mGridAdapter.addItems(getRecentItems())
            }
            Constants.viewAllCode.CATEGORY_FEATURED -> {
                isFeatured = true
                getSubCategoryProducts(true)
            }
            Constants.viewAllCode.CATEGORY_NEWEST -> {
                getSubCategoryProducts(false)
            }
            Constants.viewAllCode.OFFERS -> {
                getOffers()
            }
            FEATURED -> {
                isFeatured = true
                listAllProducts(mId)
            }
            NEWEST -> {
                listAllProducts(mId)
            }
            else -> {
                listAllProducts(mId)
            }
        }
    }

    private fun getOffers() {
        listAllProducts {
            mListAdapter.clearData()
            mGridAdapter.clearData()
            list.clear()
            it.forEach { model ->
                if (model.on_sale) {
                    list.add(model)
                    mListAdapter.addNewItem(model)
                    mGridAdapter.addNewItem(model)
                }
            }
            getFilterAttributes()

        }
    }

    private var list = ArrayList<ProductModel>()


    private fun getAdapter(): BaseRecyclerAdapter<ProductModel, ItemNewestProductBinding> {

        return object : BaseRecyclerAdapter<ProductModel, ItemNewestProductBinding>() {

            override fun onItemClick(
                    view: View,
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemNewestProductBinding
            ) {
                when (view.id) {
                    R.id.ivDislike -> {
                        (activity as AppBaseActivity).addToWishList(model, onApiSuccess = {
                            dataBinding.ivDislike.hide()
                            dataBinding.ivlike.show()
                        })
                    }
                    R.id.ivlike -> {
                        dataBinding.ivDislike.show()
                        dataBinding.ivlike.hide()
                    }
                    R.id.listProductRaw -> {
                        (activity as AppBaseActivity).showProductDetail(model)
                    }
                }
            }

            override val layoutResId: Int = R.layout.item_newest_product

            override fun onBindData(
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemNewestProductBinding
            ) {
                val mStringBuffer = StringBuilder()
                mProductModel = model
                if (model.images.isNotEmpty()) {
                    dataBinding.ivProduct.loadImageFromUrl(model.images[0].src)
                }
                if (model.on_sale)
                    dataBinding.tvProductPrice.text = model.sale_price.currencyFormat()
                else
                    dataBinding.tvProductPrice.text = model.price.currencyFormat()

                dataBinding.tvProductActualPrice.text = model.regular_price.currencyFormat()
                dataBinding.tvProductActualPrice.applyStrike()
                dataBinding.llProductColor.removeAllViews()
                if (model.attributes.isNotEmpty()) {
                    for (i in 0 until model.attributes.size) {
                        if (model.attributes[i].name == "Size") {
                            model.attributes[i].options.forEach {
                                mStringBuffer.append("$it  ")
                            }
                            dataBinding.tvSize.text = mStringBuffer

                        }
                        if (model.attributes[i].name == "Color") {
                            model.attributes[i].options.forEach {
                                if (it.contains("#")) {
                                    val view1: View =
                                            layoutInflater.inflate(
                                                    R.layout.layout_color,
                                                    dataBinding.llProductColor,
                                                    false
                                            )

                                    view1.ivColor.changeBackgroundTint(Color.parseColor(it))
                                    dataBinding.llProductColor.addView(view1)
                                }
                            }
                        }
                    }
                }
            }

            override fun onItemLongClick(view: View, model: ProductModel, position: Int) {}
        }
    }

    private fun getGridAdapter(): BaseRecyclerAdapter<ProductModel, ItemViewproductgridBinding> {

        return object : BaseRecyclerAdapter<ProductModel, ItemViewproductgridBinding>() {

            override val layoutResId: Int = R.layout.item_viewproductgrid

            override fun onBindData(
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemViewproductgridBinding
            ) {

                if (model.on_sale)
                    dataBinding.tvDiscountPrice.text = model.sale_price.currencyFormat()
                else
                    dataBinding.tvDiscountPrice.text = model.price.currencyFormat()
                dataBinding.tvOriginalPrice.text = model.regular_price.currencyFormat()
                dataBinding.tvOriginalPrice.applyStrike()
                if (model.images.isNotEmpty()) {
                    dataBinding.ivProduct.loadImageFromUrl(model.images[0].src)
                }
                dataBinding.llDynamicProductColor.removeAllViews()
                if (model.attributes.isNotEmpty()) {
                    model.attributes[0].options.forEach {
                        if (it.contains("#")) {
                            val view1: View =
                                    layoutInflater.inflate(
                                            R.layout.layout_color,
                                            dataBinding.llDynamicProductColor,
                                            false
                                    )

                            view1.ivColor.changeBackgroundTint(Color.parseColor(it))
                            dataBinding.llDynamicProductColor.addView(view1)
                        }
                    }
                }
            }

            override fun onItemClick(
                    view: View,
                    model: ProductModel,
                    position: Int,
                    dataBinding: ItemViewproductgridBinding
            ) {
                when (view.id) {
                    R.id.ivFavourite -> mFlag = if (mFlag) {
                        (activity as AppBaseActivity).addToWishList(model, onApiSuccess = {
                            dataBinding.ivFavourite.setImageResource(R.drawable.ic_heart_fill)
                            dataBinding.ivFavourite.applyColorFilter(activity!!.color(R.color.colorPrimary))
                            dataBinding.ivFavourite.setStrokedBackground(activity!!.color(R.color.favourite_backround))
                        })
                        false
                    } else {
                        dataBinding.ivFavourite.setImageResource(R.drawable.ic_heart)

                        dataBinding.ivFavourite.applyColorFilter(activity!!.color(R.color.textColorSecondary))
                        dataBinding.ivFavourite.setStrokedBackground(activity!!.color(R.color.favourite_unselected_background))
                        true
                    }
                    R.id.gridProduct -> (activity as AppBaseActivity).showProductDetail(model)
                }
            }

            override fun onItemLongClick(view: View, model: ProductModel, position: Int) {}
        }
    }

    private fun setClickEventListener() {
        ivGrid.onClick {
            setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
            ivList.setColorFilter(activity!!.color(R.color.textColorSecondary))
            rvNewestProduct.apply {
                layoutManager = GridLayoutManager(activity, 2)
                setHasFixedSize(true)
                // mGridAdapter.addItems(arguments?.getSerializable(Constants.KeyIntent.PRODUCTDATA) as java.util.ArrayList<ProductModel>)
                rvNewestProduct.adapter = mGridAdapter
                rvNewestProduct.rvItemAnimation()
            }

        }
        ivList.onClick {
            setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
            ivGrid.setColorFilter(ContextCompat.getColor(context, R.color.textColorSecondary))
            rvNewestProduct.apply {
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)
                rvNewestProduct.adapter = mListAdapter
                rvNewestProduct.rvItemAnimation()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
        val menuItem = menu.findItem(R.id.action_filter)
        val menuWishItem = menu.findItem(R.id.action_cart)
        if (mId != Constants.viewAllCode.RECENTSEARCH) {
            menuItem.isVisible = true
        }
        menuWishItem.isVisible = true
        menuCart = menuWishItem.actionView
        menuCart!!.ivCart.setColorFilter(activity!!.color(R.color.textColorPrimary))
        menuWishItem.actionView.onClick {
            activity!!.launchActivity<MyCartActivity> { }
        }
        setCartCount()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                activity!!.launchActivity<SearchActivity>()
                true
            }
            R.id.action_filter -> {
                if (filterFragment == null) {
                    filterFragment = FilterFragment.newInstance(map, categoryList)
                    filterFragment!!.mListener = listener
                }
                filterFragment!!.show(activity!!.supportFragmentManager, "Filter")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setCartCount() {
        val count = getCartCount()
        if (menuCart != null) {
            menuCart!!.tvNotificationCount.text = count
            if (count.checkIsEmpty() || count == "0") {
                menuCart!!.tvNotificationCount.hide()
            } else {
                menuCart!!.tvNotificationCount.show()
            }
        }

    }

    private fun listAllProducts(mId: Int) {
        listAllProducts {
            mListAdapter.clearData()
            mGridAdapter.clearData()
            list.clear()

            when (mId) {
                FEATURED -> {
                    it.forEach { model ->
                        if (model.featured) {
                            list.add(model)
                            mListAdapter.addNewItem(model)
                            mGridAdapter.addNewItem(model)
                        }
                    }
                }
                NEWEST -> {
                    list.addAll(it)
                    mListAdapter.addItems(it)
                    mGridAdapter.addItems(it)
                }
            }
            getFilterAttributes()
        }
    }

    private fun getSubCategoryProducts(b: Boolean) {
        subCategoryProducts(mCategoryId!!) {
            list.clear()

            if (b) {
                mListAdapter.clearData()
                mGridAdapter.clearData()
                it.forEach { model ->
                    if (model.featured) {
                        list.add(model)
                        mListAdapter.addNewItem(model)
                        mGridAdapter.addNewItem(model)
                    }
                }
            } else {
                list.addAll(it)
                mListAdapter.addItems(it)
                mGridAdapter.addItems(it)
            }
            getFilterAttributes()
        }

    }

    private fun getRecentItems(): ArrayList<ProductModel> {
        val list = recentProduct()
        list.reverse()
        return list
    }

    var map = HashMap<String, ArrayList<Attribute>>()
    var categoryList = ArrayList<CategoryData>()

    fun getFilterAttributes() {
        categoryFromAssets(onApiSuccess = {
            categoryList.addAll(it)
            map = attributes()
            if (filterFragment != null) {
                filterFragment!!.invalidate(map, categoryList)
            }
            hideProgress()
            Log.e("attributes", map.size.toString() + "****" + Gson().toJson(map))
        })
    }

}