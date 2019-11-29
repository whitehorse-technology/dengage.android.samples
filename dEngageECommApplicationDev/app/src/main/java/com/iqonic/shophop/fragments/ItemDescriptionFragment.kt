package com.iqonic.shophop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.FragmentItemdecriptionBinding
import com.iqonic.shophop.databinding.ItemColorBinding
import com.iqonic.shophop.databinding.ItemSizeBinding
import com.iqonic.shophop.models.Color
import com.iqonic.shophop.models.ProductModel
import com.iqonic.shophop.models.Size
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_itemdecription.*


class ItemDescriptionFragment : BaseFragment() {

    companion object {
        fun getNewInstance(model: ProductModel): ItemDescriptionFragment {
            val fragment = ItemDescriptionFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.KeyIntent.DATA, model)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var mColorFlag: Int = -1
    private var mSizeFlag: Int = -1
    lateinit var model: ProductModel
    lateinit var mainBinding: FragmentItemdecriptionBinding


    private var colorAdapter = object : BaseRecyclerAdapter<Color, ItemColorBinding>() {

        override val layoutResId: Int = R.layout.item_color

        override fun onBindData(model: Color, position: Int, dataBinding: ItemColorBinding) {
            dataBinding.viewColor.setStrokedBackground(android.graphics.Color.parseColor(model.color!!),android.graphics.Color.BLACK)
            dataBinding.ivChecked.setStrokedBackground(android.graphics.Color.parseColor(model.color!!),android.graphics.Color.BLACK)
            when {
                position == mColorFlag -> {
                    dataBinding.viewColor.hide()
                    dataBinding.ivChecked.show()

                }
                else -> {
                    dataBinding.viewColor.show()
                    dataBinding.ivChecked.hide()
                }
            }
        }

        override fun onItemClick(view: View, model: Color, position: Int, dataBinding: ItemColorBinding) {
            mColorFlag = position
            notifyDataSetChanged()
            getSelectedColors()
        }

        override fun onItemLongClick(view: View, model: Color, position: Int) {

        }

    }

    private var sizeAdapter = object : BaseRecyclerAdapter<Size, ItemSizeBinding>() {
        override val layoutResId: Int = R.layout.item_size

        override fun onBindData(model: Size, position: Int, dataBinding: ItemSizeBinding) {
            dataBinding.ivChecked.text = model.size
            when {
                position == mSizeFlag -> {
                    dataBinding.ivChecked.setTextColor(activity!!.color(R.color.white))
                    dataBinding.ivChecked.background =
                            ContextCompat.getDrawable(activity!!, R.drawable.bg_circle_primary)
                }
                else -> {
                    dataBinding.ivChecked.setTextColor(activity!!.color(R.color.textColorPrimary))
                    dataBinding.ivChecked.background = null
                }
            }
        }

        override fun onItemClick(view: View, model: Size, position: Int, dataBinding: ItemSizeBinding) {
            mSizeFlag = position
            notifyDataSetChanged()
            getSelectedSize()
        }

        override fun onItemLongClick(view: View, model: Size, position: Int) {

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_itemdecription, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = arguments?.getSerializable(Constants.KeyIntent.DATA) as ProductModel

        mainBinding.model = model

        tvFreePrice.applyStrike()
        tvFreePrice.text = model.price.currencyFormat()
        bindAdapters()
        bindData()
    }

    private fun bindData() {
        txtDescription.setText(model.short_description.getHtmlString().toString())
        val colorList = ArrayList<Color>()
        val sizeList = ArrayList<Size>()

        if (model.attributes.isNotEmpty()) {
            model.attributes.forEach {
                if (it.name == "Size") {
                    it.options.forEach { option ->
                        val mSize = Size()
                        mSize.size = option
                        sizeList.add(mSize)
                    }
                } else if (it.name == "Color") {
                    it.options.forEach { option ->
                        val color = Color()
                        color.color = option
                        colorList.add(color)
                    }
                }
            }

            if (sizeList.size == 0) {
                tvSize.hide()
            } else {
                sizeAdapter.addItems(sizeList)
            }

            if (colorList.size == 0) {
                tvcolor.hide()
            } else {
                colorAdapter.addItems(colorList)
            }
        }

    }

    private fun bindAdapters() {
        rvColors.setHorizontalLayout()
        rvSize.setHorizontalLayout()

        rvColors.adapter = colorAdapter
        rvSize.adapter = sizeAdapter
    }

    fun getSelectedColors(): String {
        return if (mColorFlag == -1) {
            ""
        } else {
            colorAdapter.mModelList[mColorFlag].color!!
        }
    }

    fun getSelectedSize(): String {
        return if (mSizeFlag == -1) {
            ""
        } else {
            sizeAdapter.mModelList[mSizeFlag].size!!
        }
    }

    fun isColorAvailable(): Boolean {
        return colorAdapter.mModelList.size != 0
    }

    fun isSizeAvailable(): Boolean {
        return sizeAdapter.mModelList.size != 0
    }


}