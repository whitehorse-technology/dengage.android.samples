package com.iqonic.shophop.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemNewestProductBinding
import com.iqonic.shophop.models.ProductModel
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_color.view.*
import kotlinx.android.synthetic.main.layout_nodata.*
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : BaseFragment() {
    private lateinit var adapter: BaseRecyclerAdapter<ProductModel, ItemNewestProductBinding>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_search, container, false)
    }
    private var list=ArrayList<ProductModel>()
    private var filterList=ArrayList<ProductModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppBaseActivity).setToolbar(toolbar)
        adapter = getAdapter()
        productsFromAssets {
            list=it
        }
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!=null){
                    searchProducts(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.isEmpty()!!) {
                    if (activity != null) {
                        rlNoData.show()
                        pbLoader.hide()
                        adapter.clearData()
                    }
                } else
                    searchProducts(newText)

                return true
            }

        })
        aSearch_rvSearch.setVerticalLayout()
        aSearch_rvSearch.adapter = adapter
        pbLoader.hide()
        //searchProducts(searchView.query!!.toString())
    }

    private fun searchProducts(query: String) {
        filterList.clear()
        list.forEach {
            if (it.name.toLowerCase(Locale.getDefault()).contains(query)){
                filterList.add(it)
            }
        }
        if (filterList.size == 0) {
            rlNoData.show()
            pbLoader.hide()
            adapter.clearData()
        } else {
            rlNoData.hide()
            pbLoader.hide()
            adapter.addItems(filterList)
        }
    }

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

            override fun onBindData(model: ProductModel, position: Int, dataBinding: ItemNewestProductBinding) {
                val mStringBuffer = StringBuilder()

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
                                            layoutInflater.inflate(R.layout.layout_color, dataBinding.llProductColor, false)

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
}
