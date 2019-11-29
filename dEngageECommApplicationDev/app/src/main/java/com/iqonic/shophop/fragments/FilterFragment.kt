package com.iqonic.shophop.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.iqonic.shophop.R
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.models.*
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.iqonic.shophop.databinding.*
import kotlinx.android.synthetic.main.layout_colors.*
import kotlinx.android.synthetic.main.layout_filter.*
import kotlinx.android.synthetic.main.layout_size.*


class FilterFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            map: HashMap<String, ArrayList<Attribute>>,
            categoryList: ArrayList<CategoryData>
        ): FilterFragment = FilterFragment().apply {
            val bundle = Bundle()
            bundle.putSerializable(Constants.KeyIntent.DATA, map)
            bundle.putSerializable(Constants.KeyIntent.CATEGORY_DATA, categoryList)
            arguments = bundle
        }
    }

    private lateinit var map: HashMap<String, ArrayList<Attribute>>
    private val priceArray =
        arrayOf("$0", "$2", "$5", "$7", "$10", "$12", "$15", "$17", "$20", "$25", "$30")
    private val priceArray2 = arrayOf("0", "2", "5", "7", "10", "12", "15", "17", "20", "25", "30")
    private val ratings = arrayOf("4 star & above", "3 star & above", "2 star & above", "1 star & above")
    private lateinit var categoryList: ArrayList<CategoryData>
    private val brandColors = arrayOf(
        R.color.cat_1,
        R.color.cat_2,
        R.color.cat_3,
        R.color.cat_4
    )
    var mListener: FilterListener? = null

    private var subCategoryAdapter = object : BaseRecyclerAdapter<CategoryData, ItemFilterCategoryBinding>() {
        override val layoutResId: Int = R.layout.item_filter_category

        override fun onBindData(model: CategoryData, position: Int, dataBinding: ItemFilterCategoryBinding) {
            if (model.isSelected) {
                dataBinding.tvSubCategory.setTextColor(activity!!.color(R.color.white))
                dataBinding.tvSubCategory.setStrokedBackground(activity!!.color(R.color.colorPrimary))
            } else {
                dataBinding.tvSubCategory.setTextColor(activity!!.color(R.color.colorPrimary))
                dataBinding.tvSubCategory.setStrokedBackground(
                    activity!!.color(R.color.white),
                    activity!!.color(R.color.colorPrimary)
                )
            }
        }

        override fun onItemClick(view: View, model: CategoryData, position: Int, dataBinding: ItemFilterCategoryBinding) {
            mModelList[position].isSelected = !(model.isSelected)
            notifyItemChanged(position)
        }

        override fun onItemLongClick(view: View, model: CategoryData, position: Int) {

        }
    }

    private var brandAdapter = object : BaseRecyclerAdapter<Brand, ItemFilterBrandBinding>() {
        override val layoutResId: Int = R.layout.item_filter_brand

        override fun onBindData(model: Brand, position: Int, dataBinding: ItemFilterBrandBinding) {
            when {
                model.isSelected!! -> {
                    dataBinding.tvBrandName.setTextColor(activity!!.color(model.color!!))
                    dataBinding.ivSelect.setImageResource(R.drawable.ic_check)
                    dataBinding.ivSelect.setColorFilter(activity!!.color(model.color!!))
                    dataBinding.ivSelect.setStrokedBackground(
                        activity!!.color(model.color!!),
                        activity!!.color(model.color!!),
                        0.4f
                    )
                }
                else -> {
                    dataBinding.tvBrandName.setTextColor(activity!!.color(R.color.textColorSecondary))
                    dataBinding.ivSelect.setImageResource(0)
                    dataBinding.ivSelect.setStrokedBackground(activity!!.color(R.color.checkbox_color))
                }
            }
        }

        override fun onItemClick(view: View, model: Brand, position: Int, dataBinding: ItemFilterBrandBinding) {
            mModelList[position].isSelected = !(model.isSelected!!)
            notifyItemChanged(position)
        }

        override fun onItemLongClick(view: View, model: Brand, position: Int) {

        }
    }

    private var colorAdapter = object : BaseRecyclerAdapter<Color, ItemColorBinding>() {
        override val layoutResId: Int = R.layout.item_color

        override fun onBindData(model: Color, position: Int, dataBinding: ItemColorBinding) {
            dataBinding.viewColor.changeBackgroundTint(model.colorName!!)
            dataBinding.ivChecked.changeBackgroundTint(model.colorName!!)
            when {
                model.isSelected!! -> {
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
            mModelList[position].isSelected = !(model.isSelected!!)
            notifyItemChanged(position)
        }

        override fun onItemLongClick(view: View, model: Color, position: Int) {

        }
    }

    private var sizeAdapter = object : BaseRecyclerAdapter<Size, ItemSizeBinding>() {
        override val layoutResId: Int = R.layout.item_size

        override fun onBindData(model: Size, position: Int, dataBinding: ItemSizeBinding) {
            dataBinding.ivChecked.text = model.sizeName
            when {
                model.isSelected!! -> {
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
            mModelList[position].isSelected = !(model.isSelected!!)
            notifyItemChanged(position)
        }

        override fun onItemLongClick(view: View, model: Size, position: Int) {

        }
    }

    private var discountAdapter = getExpandableAdapter()

    private var ratingAdapter = getExpandableAdapter()

    private var rotateAnimation: RotateAnimation? = getRotationAnimation(0f, 180f)
    private var rotateAnimation2: RotateAnimation? = getRotationAnimation(180f, 360f)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = arguments?.getSerializable(Constants.KeyIntent.DATA) as HashMap<String, ArrayList<Attribute>>
        categoryList = arguments?.getSerializable(Constants.KeyIntent.CATEGORY_DATA) as ArrayList<CategoryData>
        bindAdapters()
        bindData()
        bindClickListeners()
    }

    private fun bindClickListeners() {
        ivClose.onClick {
            dismiss()
        }
        rlDiscount.onClick {
            if (rcvDiscount.isVisible()) {
                ivExpandDiscount.startAnimation(rotateAnimation)
                invalidateView(rcvDiscount, false)
            } else {
                ivExpandDiscount.startAnimation(rotateAnimation2)
                invalidateView(rcvDiscount, true)

            }
        }
        rlRatings.onClick {
            if (rcvRatings.isVisible()) {
                ivExpandRating.startAnimation(rotateAnimation)
                invalidateView(rcvRatings, false)
            } else {
                ivExpandRating.startAnimation(rotateAnimation2)
                invalidateView(rcvRatings, true)
            }
        }
        tvSelectAll.onClick {
            brandAdapter.mModelList.forEach {
                it.isSelected = true
            }
            brandAdapter.notifyDataSetChanged()
        }
        tvShowMore.onClick {
            if (subCategoryAdapter.size == 5) {
                //expand
                subCategoryAdapter.setModelSize(categoryList.size)
                tvShowMore.text = context.getString(R.string.lbl_less)
            } else {
                //collapse
                subCategoryAdapter.setModelSize(5)
                tvShowMore.text = context.getString(R.string.lbl_more)
            }
            subCategoryAdapter.notifyDataSetChanged()

        }
        tvReset.onClick {
            resetSubcategory()
            resetPrice()
            resetBrands()
            resetColors()
            resetSize()
            resetDiscount()
            resetRatings()
        }
        tvApply.onClick {
            if (mListener != null) {

                val brands = ArrayList<String>()
                brandAdapter.mModelList.forEach {
                    if (it.isSelected!!) {
                        brands.add(it.brandName!!)
                    }
                }
                Log.e("brands", Gson().toJson(brands))

                val colors = ArrayList<String>()
                colorAdapter.mModelList.forEach {
                    if (it.isSelected!!) {
                        colors.add(it.color!!)
                    }
                }

                Log.e("colors", Gson().toJson(colors))

                val sizes = ArrayList<String>()
                sizeAdapter.mModelList.forEach {
                    if (it.isSelected!!) {
                        sizes.add(it.sizeName!!)
                    }
                }

                val cateogry = ArrayList<String>()
                subCategoryAdapter.mModelList.forEach {
                    if (it.isSelected) {
                        cateogry.add(it.name)
                    }
                }

                Log.e("cateogry", Gson().toJson(cateogry))
                mListener?.apply(
                    priceArray2[rangebar1.leftPinValue.toInt()].toInt(),
                    priceArray2[rangebar1.rightPinValue.toInt()].toInt(),
                    brands,
                    colors,
                    sizes,
                    cateogry
                )
            }
            dismiss()
        }
    }

    private fun resetRatings() {
        ratingAdapter.mModelList.forEach {
            it.isSelected = false
        }
        ratingAdapter.notifyDataSetChanged()
    }

    private fun resetDiscount() {
        discountAdapter.mModelList.forEach {
            it.isSelected = false
        }
        discountAdapter.notifyDataSetChanged()
    }

    private fun resetSize() {
        sizeAdapter.mModelList.forEach {
            it.isSelected = false
        }
        sizeAdapter.notifyDataSetChanged()
    }

    private fun resetColors() {
        colorAdapter.mModelList.forEach {
            it.isSelected = false
        }
        colorAdapter.notifyDataSetChanged()

    }

    private fun resetBrands() {
        brandAdapter.mModelList.forEach {
            it.isSelected = false
        }
        brandAdapter.notifyDataSetChanged()
    }

    private fun resetPrice() {

    }

    private fun resetSubcategory() {
        subCategoryAdapter.mModelList.forEach {
            it.isSelected = false
        }
        subCategoryAdapter.notifyDataSetChanged()
    }

    private fun invalidateView(recyclerView: RecyclerView?, b: Boolean) {
        runDelayed(200) {
            if (!b) {
                recyclerView?.hide()
            } else {
                recyclerView?.show()
            }
        }
    }

    private fun getRotationAnimation(from: Float, to: Float): RotateAnimation? {
        val animation = RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 300
        animation.fillAfter = true
        animation.interpolator = LinearInterpolator()
        return animation
    }

    private fun bindData() {
        if (rangebar1 != null) {
            rangebar1.tickTopLabels = priceArray

            subCategoryAdapter.setModelSize(5)
            subCategoryAdapter.addItems(categoryList)
            if (map.containsKey("Brand")) {
                val brandList = ArrayList<Brand>()
                map["Brand"]?.forEachIndexed { index, s ->
                    val brand = Brand()
                    brand.brandName = s.name
                    brand.id = s.id
                    brand.color = brandColors[index % brandColors.size]
                    brandList.add(brand)
                }
                brandAdapter.addItems(brandList)
            }
            if (map.containsKey("Color")) {
                val colorList = ArrayList<Color>()
                map["Color"]?.forEach { s ->
                    if (s.name.isValidColor()) {
                        val color = Color()
                        color.colorName = android.graphics.Color.parseColor(s.name)
                        color.id = s.id
                        color.color=s.name
                        colorList.add(color)
                    }
                }
                colorAdapter.addItems(colorList)
            }
            if (map.containsKey("Size")) {
                val sizeList = ArrayList<Size>()
                map["Size"]?.forEach { s ->
                    val size = Size()
                    size.sizeName = s.name
                    size.id = s.id
                    size.isSelected = false
                    sizeList.add(size)
                }
                sizeAdapter.addItems(sizeList)
            }

            val ratingList = ArrayList<Discount>()

            ratings.forEach {
                val size = Discount()
                size.discount = it
                ratingList.add(size)
            }
            ratingAdapter.addItems(ratingList)
        }

    }

    private fun bindAdapters() {
        val layoutManager = ChipsLayoutManager.newBuilder(activity)
            .setOrientation(ChipsLayoutManager.HORIZONTAL)
            .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
            .withLastRow(false)
            .build()
        rcvSubCategories.layoutManager = layoutManager
        rcvSubCategories.itemAnimator = null
        rcvSubCategories.adapter = subCategoryAdapter

        rcvBrands.setVerticalLayout()
        rcvColors.setHorizontalLayout()
        rcvSize.setHorizontalLayout()
        rcvDiscount.setVerticalLayout()
        rcvRatings.setVerticalLayout()

        rcvBrands.adapter = brandAdapter
        rcvColors.adapter = colorAdapter
        rcvSize.adapter = sizeAdapter
        rcvDiscount.adapter = discountAdapter
        rcvRatings.adapter = ratingAdapter
    }

    private fun getExpandableAdapter(): BaseRecyclerAdapter<Discount, ItemFilterDiscountBinding> {
        return object : BaseRecyclerAdapter<Discount, ItemFilterDiscountBinding>() {
            override val layoutResId: Int = R.layout.item_filter_discount

            override fun onBindData(model: Discount, position: Int, dataBinding: ItemFilterDiscountBinding) {
                when {
                    model.isSelected!! -> {
                        dataBinding.tvDiscount.setTextColor(activity!!.color(R.color.textColorPrimary))
                        dataBinding.ivSelect.setImageResource(R.drawable.ic_check)
                        dataBinding.ivSelect.setStrokedBackground(
                            activity!!.color(R.color.textColorPrimary),
                            activity!!.color(R.color.textColorPrimary),
                            0.4f
                        )
                    }
                    else -> {
                        dataBinding.tvDiscount.setTextColor(activity!!.color(R.color.textColorSecondary))
                        dataBinding.ivSelect.setImageResource(0)
                        dataBinding.ivSelect.changeBackgroundTint(activity!!.color(R.color.checkbox_color))
                    }
                }
            }

            override fun onItemClick(
                view: View,
                model: Discount,
                position: Int,
                dataBinding: ItemFilterDiscountBinding
            ) {
                mModelList[position].isSelected = !(model.isSelected!!)
                notifyItemChanged(position)
            }

            override fun onItemLongClick(view: View, model: Discount, position: Int) {

            }
        }
    }

    fun invalidate(map: HashMap<String, ArrayList<Attribute>>, categoryList: ArrayList<CategoryData>) {
        this.map = map
        this.categoryList = categoryList
        bindData()
    }

    interface FilterListener {
        fun apply(
            minPrice: Int,
            maxPrice: Int,
            selectedBrands: ArrayList<String>,
            selectedColors: ArrayList<String>,
            selectedSize: ArrayList<String>,
            selectedCategory: ArrayList<String>
        )
    }

}