package com.iqonic.shophop.fragments


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dengage.sdk.DengageEvent
import com.dengage.sdk.models.CardItem
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.activity.DashBoardActivity
import com.iqonic.shophop.activity.OrderSummaryActivity
import com.iqonic.shophop.base.BaseRecyclerAdapter
import com.iqonic.shophop.databinding.ItemCartBinding
import com.iqonic.shophop.models.Key
import com.iqonic.shophop.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_cart.*
import java.util.HashMap


class MyCartFragment : BaseFragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    private var mCartAdapter: BaseRecyclerAdapter<Key, ItemCartBinding> =
            object : BaseRecyclerAdapter<Key, ItemCartBinding>() {
                override val layoutResId: Int = R.layout.item_cart

                override fun onBindData(model: Key, position: Int, dataBinding: ItemCartBinding) {
                    dataBinding.llButton.show()
                    dataBinding.llMoveTocart.hide()
                    dataBinding.llNextTimeBuy.show()
                    dataBinding.tvOriginalPrice.applyStrike()
                    if (model.sale_price.isNotEmpty()) {
                        dataBinding.tvPrice.text =
                                (model.sale_price.toDouble() * model.quantity).toString().currencyFormat()
                    }
                    if (model.product_price.isNotEmpty()) {
                        dataBinding.tvOriginalPrice.text =
                                (model.product_price.toDouble() * model.quantity).toString().currencyFormat()
                    }
                    if (model.product_color.isNotEmpty()) {
                        dataBinding.ivChecked.changeBackgroundTint(
                                android.graphics.Color.parseColor(
                                        model.product_color
                                )
                        )
                    }
                    dataBinding.ivProduct.loadImageFromUrl(model.product_image)
                    dataBinding.qtySpinner.text = model.quantity.toString()
                }

                override fun onItemClick(view: View, model: Key, position: Int, dataBinding: ItemCartBinding) {
                    when (view.id) {
                        R.id.llRemove -> {
                            removeCartItem(model, false)
                        }
                        R.id.ll_qty -> activity?.showQtyChangeDialog {
                            model.quantity = it.toInt()
                            notifyItemChanged(position)
                            updateCartItem(model)
                        }
                        R.id.llNextTimeBuy -> {
                            removeCartItem(model, true)
                        }
                    }
                }

                override fun onItemLongClick(view: View, model: Key, position: Int) {}
            }

    private var mNextTimeBuyAdapter = object : BaseRecyclerAdapter<Key, ItemCartBinding>() {
        override val layoutResId: Int = R.layout.item_cart

        override fun onBindData(model: Key, position: Int, dataBinding: ItemCartBinding) {
            dataBinding.llButton.show()
            dataBinding.llMoveTocart.show()
            dataBinding.llNextTimeBuy.hide()
            dataBinding.ivProduct.loadImageFromUrl(model.product_image)
            dataBinding.tvOriginalPrice.applyStrike()
            dataBinding.tvPrice.text = model.sale_price.currencyFormat()
            dataBinding.tvOriginalPrice.text = model.product_price.currencyFormat()
            dataBinding.ivChecked.changeBackgroundTint(android.graphics.Color.parseColor(model.product_color))
            dataBinding.qtySpinner.text = model.quantity.toString()
        }

        override fun onItemClick(view: View, model: Key, position: Int, dataBinding: ItemCartBinding) {
            when (view.id) {
                R.id.ll_qty -> activity?.showQtyChangeDialog {
                    mModelList[position].quantity = it.toInt()
                    notifyItemChanged(position)
                }
                R.id.llMoveTocart -> {
                    removeItem(position)
                    moveItemToCart(model)
                }
                R.id.llRemove -> {
                    removeItem(position)
                    setNextTimeBuyProducts(mModelList)
                    if (itemCount == 0) {
                        tvNextTimeBuy.hide()
                    } else {
                        tvNextTimeBuy.show()
                    }

                }
            }
        }

        override fun onItemLongClick(view: View, model: Key, position: Int) {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        var items = ArrayList<CardItem>()
        val list = getCartList()
        var totalPrice = 0.0
        list.forEach {
            val item = CardItem()
            item.variantId = it.variation_id.toString()
            item.quantity = it.quantity
            item.productId = it.product_id.toString()
            item.currency = "usd"
            item.price = it.product_price.toDouble()
            item.discountedPrice = it.sale_price.toDouble()
            items.add(item)

            if (it.sale_price.isNotEmpty()) {
                totalPrice += it.sale_price.toDouble() * it.quantity
            } else {
                if (it.product_price.isNotEmpty()) {
                    totalPrice += it.product_price.toDouble() * it.quantity
                }
            }
        };

        var data : HashMap<String, Any>
                = HashMap<String, Any> ();

        var cartItems : MutableList<HashMap<String, Any>> = emptyList<HashMap<String, Any>>().toMutableList();

        val keys = getCartList()
        keys.forEach {
            var cartItem : HashMap<String, Any>
                    = HashMap<String, Any> ();

            cartItem.put("product_id", it.product_id);
            cartItem.put("product_variant_id", it.variation_id);
            cartItem.put("quantity", it.quantity);
            cartItem.put("unit_price", it.product_price.toDouble());
            cartItem.put("discounted_price", it.sale_price.toDouble());

            cartItems.add(cartItem);
        }
        data.put("cartItems", cartItems.toTypedArray());

        DengageEvent.getInstance(activity?.applicationContext).viewCart(data);

        rvCart.setVerticalLayout()
        rvNextTimeBuy.setVerticalLayout()
        rvCart.adapter = mCartAdapter
        rvNextTimeBuy.adapter = mNextTimeBuyAdapter
        btnShopNow.onClick {
            if (activity!! is DashBoardActivity) {
                (activity!! as DashBoardActivity).loadHomeFragment()
            }

        }
        tvContinue.onClick {
            activity?.launchActivity<OrderSummaryActivity> { }
        }
        llSeePriceDetail.onClick {
            scrollToPriceDetail()
        }
    }

    private fun setItems() {
        mNextTimeBuyAdapter.addItems(nextTimeBuyProducts())
        setCart()

    }

    private fun scrollToPriceDetail() {
        Handler().post {
            nsvCart.scrollTo(0, llPayment.top)
        }
    }


    private fun invalidateCartLayout(it: ArrayList<Key>) {
        if (activity != null) {
            if (it.size == 0) {
                invalidatePaymentLayout(false)
                if (mNextTimeBuyAdapter.itemCount == 0) {
                    llNoItems.show()
                    tvNextTimeBuy.hide()

                } else {
                    llNoItems.hide()
                    tvNextTimeBuy.show()
                }
            } else {
                llNoItems.hide()
                if (mNextTimeBuyAdapter.itemCount == 0) {
                    tvNextTimeBuy.hide()
                } else {
                    tvNextTimeBuy.show()
                }
                tvTotalCartAmount.text = (activity as AppBaseActivity).getCartTotal().toString().currencyFormat()
                invalidatePaymentLayout(true)
                mCartAdapter.addItems(it)
            }

        }
    }

    private fun invalidatePaymentLayout(b: Boolean) {
        if (activity != null) {
            if (!b) {
                llPayment.hide()
                lay_button.hide()
                rvCart.hide()
            } else {
                llPayment.show()
                lay_button.show()
                rvCart.show()
            }
        }

    }

    private fun removeCartItem(model: Key, shouldNextTimeBuy: Boolean) {
        activity!!.getAlertDialog(
                getString(R.string.msg_confirmation),
                onPositiveClick = { _, _ ->
                    val key = Key()
                    // TODO: event remove cart
                    key.product_id = model.product_id
                    (activity as AppBaseActivity).removeItem(key)
                    snackBar(activity!!.getString(R.string.success))
                    if (shouldNextTimeBuy) {
                        tvNextTimeBuy.show()
                        mNextTimeBuyAdapter.addNewItem(model)
                        addNextTimeBuyProduct(model)
                    }
                },
                onNegativeClick = { dialog, _ ->
                    dialog.dismiss()
                }).show()
    }

    private fun moveItemToCart(model: Key) {
        (activity as AppBaseActivity).addCart(model)
        setNextTimeBuyProducts(mNextTimeBuyAdapter.mModelList)
    }

    private fun updateCartItem(model: Key) {
        updateItem(model)
        tvTotalCartAmount.text = (activity as AppBaseActivity).getCartTotal().toString().currencyFormat()
    }

    override fun onResume() {
        super.onResume()
        setItems()
    }
    fun setCart() {
        if (activity!=null){
            invalidateCartLayout(getCartList())
        }
    }
}
