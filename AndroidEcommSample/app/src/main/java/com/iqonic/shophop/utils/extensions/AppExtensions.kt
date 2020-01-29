package com.iqonic.shophop.utils.extensions

import android.app.Activity
import android.content.*
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.iqonic.shophop.AppBaseActivity
import com.iqonic.shophop.R
import com.iqonic.shophop.ShopHopApp
import com.iqonic.shophop.ShopHopApp.Companion.getAppInstance
import com.iqonic.shophop.activity.ProductDetailActivity
import com.iqonic.shophop.models.*
import com.iqonic.shophop.utils.Constants
import com.iqonic.shophop.utils.Constants.AppBroadcasts.CARTITEM_UPDATE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.ORDER_COUNT_CHANGE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.PROFILE_UPDATE
import com.iqonic.shophop.utils.Constants.AppBroadcasts.WISHLIST_UPDATE
import com.iqonic.shophop.utils.Constants.KeyIntent.IS_ADDED_TO_CART
import com.iqonic.shophop.utils.Constants.SharedPref.CART_DATA
import com.iqonic.shophop.utils.Constants.SharedPref.IS_LOGGED_IN
import com.iqonic.shophop.utils.Constants.SharedPref.KEY_CART_COUNT
import com.iqonic.shophop.utils.Constants.SharedPref.KEY_NEXT_TIME_BUY
import com.iqonic.shophop.utils.Constants.SharedPref.KEY_RECENTS
import com.iqonic.shophop.utils.Constants.SharedPref.KEY_USER_ADDRESS
import com.iqonic.shophop.utils.Constants.SharedPref.KEY_USER_CART
import com.iqonic.shophop.utils.Constants.SharedPref.USER_DISPLAY_NAME
import com.iqonic.shophop.utils.Constants.SharedPref.USER_EMAIL
import com.iqonic.shophop.utils.Constants.SharedPref.USER_FIRST_NAME
import com.iqonic.shophop.utils.Constants.SharedPref.USER_ID
import com.iqonic.shophop.utils.Constants.SharedPref.USER_LAST_NAME
import com.iqonic.shophop.utils.Constants.SharedPref.USER_PASSWORD
import com.iqonic.shophop.utils.Constants.SharedPref.USER_PROFILE
import com.iqonic.shophop.utils.SharedPrefUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.layout_paymentdetail.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

fun isLoggedIn(): Boolean = getSharedPrefInstance().getBooleanValue(IS_LOGGED_IN)

fun getUserId(): String = getSharedPrefInstance().getStringValue(USER_ID)

fun getUserFullName(): String {
    return when {
        isLoggedIn() -> (getSharedPrefInstance().getStringValue(USER_FIRST_NAME, "Julie") + " " + getSharedPrefInstance().getStringValue(
                USER_LAST_NAME
                , "Deerman")).toCamelCase()
        else -> "Guest User"
    }
}


fun getFirstName(): String = getSharedPrefInstance().getStringValue(USER_FIRST_NAME)

fun getLastName(): String = getSharedPrefInstance().getStringValue(USER_LAST_NAME)

fun getEmail(): String = getSharedPrefInstance().getStringValue(USER_EMAIL)

fun changePassword(password: String) {
    getSharedPrefInstance().setValue(USER_PASSWORD, password)
}


fun getProfile(): String = getSharedPrefInstance().getStringValue(USER_PROFILE)

fun getCartCount(): String = getSharedPrefInstance().getIntValue(KEY_CART_COUNT, 0).toString()

fun AppBaseActivity.getCartTotal(): Int {

    val list = getCartList()
    var count = 0
    list.forEach {
        if (it.sale_price.isNotEmpty()) {
            count += it.sale_price.toInt() * it.quantity
        } else {
            if (it.product_price.isNotEmpty()) {
                count += it.product_price.toInt() * it.quantity
            }
        }
    }
    tvOffer.text = getString(R.string.text_offer_not_available)
    tvShippingCharge.text = getString(R.string.lbl_free)
    tvTotalAmount.text = count.toString().currencyFormat()
    return count
}


fun addAddress(address: Address) {
    val list = getAddressList()
    if (list.size == 0) {
        address.isDefault = true
    }
    list.add(address)
    getSharedPrefInstance().setValue(KEY_USER_ADDRESS, Gson().toJson(list))
}

fun getCartObject(product: ProductModel, color: String, size: String): Key {
    val cartData = Key()
    cartData.product_id = product.id
    cartData.product_name = product.name
    if (product.on_sale) cartData.sale_price = product.sale_price
    else cartData.sale_price = product.sale_price
    cartData.product_price = product.regular_price
    cartData.quantity = 1
    if (product.images.isNotEmpty()) cartData.product_image = product.images[0].src
    cartData.product_color = color
    cartData.product_size = size
    return cartData
}


fun AppBaseActivity.addCart(cartData: Key) {
    val list = getCartList()
    val pos = getCartPositionIfExist(list, cartData)
    if (pos != -1) {
        list.removeAt(pos)
    }
    list.add(cartData)
    getSharedPrefInstance().setValue(KEY_USER_CART, Gson().toJson(list))
    getSharedPrefInstance().setValue(KEY_CART_COUNT, list.size)
    //  sendMyCartBroadcast()
    sendCartBroadcast()
    sendCartCountChangeBroadcast()
    snackBar(getAppInstance().getString(R.string.success_add))
}


/**
 * Add shared preference related to user session here
 */
fun clearLoginPref() {
    getSharedPrefInstance().removeKey(IS_LOGGED_IN)

}

fun getAddressList(): ArrayList<Address> {
    val string = getSharedPrefInstance().getStringValue(KEY_USER_ADDRESS, "")
    if (string.isEmpty()) {
        return ArrayList()
    }
    return Gson().fromJson(string, object : TypeToken<ArrayList<Address>>() {}.type)
}

fun getCartList(): ArrayList<Key> {
    val string = getSharedPrefInstance().getStringValue(KEY_USER_CART, "")
    if (string.isEmpty()) {
        return ArrayList()
    }

    return Gson().fromJson(string, object : TypeToken<ArrayList<Key>>() {}.type)

}

fun getCartPositionIfExist(list: ArrayList<Key>, product: Key): Int {
    list.forEachIndexed { i: Int, productModel: Key ->
        if (product.product_id == productModel.product_id) {
            return i
        }
    }
    return -1
}

fun AppBaseActivity.removeItem(product: Key) {
    val list = getCartList()
    val pos = getCartPositionIfExist(list, product)
    if (pos != -1) {
        list.removeAt(pos)
    }
    getSharedPrefInstance().setValue(KEY_USER_CART, Gson().toJson(list))
    getSharedPrefInstance().setValue(KEY_CART_COUNT, list.size)
    sendCartCountChangeBroadcast()
    sendCartBroadcast()
}

fun updateItem(product: Key) {
    val list = getCartList()
    val pos = getCartPositionIfExist(list, product)
    if (pos != -1) {
        list[pos] = product
    }
    getSharedPrefInstance().setValue(KEY_USER_CART, Gson().toJson(list))
}

fun setAddressList(list: ArrayList<Address>) =
        getSharedPrefInstance().setValue(KEY_USER_ADDRESS, Gson().toJson(list))

fun getSharedPrefInstance(): SharedPrefUtils {
    return if (ShopHopApp.sharedPrefUtils == null) {
        ShopHopApp.sharedPrefUtils = SharedPrefUtils()
        ShopHopApp.sharedPrefUtils!!
    } else {
        ShopHopApp.sharedPrefUtils!!
    }
}

fun RecyclerView.rvItemAnimation() {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
}

fun ImageView.loadImageFromUrl(aImageUrl: String, aPlaceHolderImage: Int = R.drawable.placeholder, aErrorImage: Int = R.drawable.placeholder) {
    if (!aImageUrl.checkIsEmpty()) {
        Glide.with(getAppInstance())
                .load(Uri.parse("file:///android_asset/data/products$aImageUrl"))
                .placeholder(aPlaceHolderImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(aErrorImage)
                .into(this)
    } else {
        displayBlankImage(getAppInstance(), aPlaceHolderImage)
    }
}

fun ImageView.displayBlankImage(aContext: Context, aPlaceHolderImage: Int) {
    Glide.with(aContext)
            .load(aPlaceHolderImage)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
}

fun Context.fontSemiBold(): Typeface? {
    return Typeface.createFromAsset(assets, "fonts/Montserrat-SemiBold.ttf")
}

fun Context.fontBold(): Typeface? {
    return Typeface.createFromAsset(assets, "fonts/Montserrat-Bold.ttf")
}

fun Activity.makeTransparentStatusBar() {
    val window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = this.getColor(R.color.item_background)
    } else {
        window.statusBarColor = ContextCompat.getColor(this,R.color.item_background)

    }
}

fun Activity.getAlertDialog(aMsgText: String, aTitleText: String = getString(R.string.lbl_dialog_title), aPositiveText: String = getString(R.string.lbl_yes), aNegativeText: String = getString(R.string.lbl_no), onPositiveClick: (dialog: DialogInterface, Int) -> Unit, onNegativeClick: (dialog: DialogInterface, Int) -> Unit): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(aTitleText)
    builder.setMessage(aMsgText)
    builder.setPositiveButton(aPositiveText) { dialog, which ->
        onPositiveClick(dialog, which)
    }
    builder.setNegativeButton(aNegativeText) { dialog, which ->
        onNegativeClick(dialog, which)
    }
    return builder.create()
}

fun recentProduct(): ArrayList<ProductModel> {
    val string = getSharedPrefInstance().getStringValue(KEY_RECENTS, "")
    if (string.isEmpty()) {
        return ArrayList()
    }
    return Gson().fromJson(string, object : TypeToken<ArrayList<ProductModel>>() {}.type)
}

fun addToRecentProduct(product: ProductModel) {
    val list = recentProduct()
    val pos = getPositionIfExist(list, product)
    if (pos != -1) {
        list.removeAt(pos)
    }
    list.add(product)
    getSharedPrefInstance().setValue(KEY_RECENTS, Gson().toJson(list))
}

fun getPositionIfExist(list: ArrayList<ProductModel>, product: ProductModel): Int {
    list.forEachIndexed { i: Int, productModel: ProductModel ->
        if (product.id == productModel.id) {
            return i
        }
    }
    return -1
}

fun nextTimeBuyProducts(): ArrayList<Key> {
    val string = getSharedPrefInstance().getStringValue(KEY_NEXT_TIME_BUY, "")
    if (string.isEmpty()) {
        return ArrayList()
    }
    return Gson().fromJson(string, object : TypeToken<ArrayList<Key>>() {}.type)
}

fun addNextTimeBuyProduct(product: Key) {
    val list = nextTimeBuyProducts()
    val pos = isProductExist(list, product)
    if (pos != -1) {
        list.removeAt(pos)
    }
    list.add(product)
    getSharedPrefInstance().setValue(KEY_NEXT_TIME_BUY, Gson().toJson(list))
}

fun setNextTimeBuyProducts(products: ArrayList<Key>) {
    getSharedPrefInstance().setValue(KEY_NEXT_TIME_BUY, Gson().toJson(products))
}

fun isProductExist(list: ArrayList<Key>, product: Key): Int {
    list.forEachIndexed { i: Int, productModel: Key ->
        if (product.product_id == productModel.product_id) {
            return i
        }
    }
    return -1
}

fun Activity.productLayoutParams(): LinearLayout.LayoutParams {
    val width = (getDisplayWidth() / 2.5).toInt()
    val imgHeight = width + (width / 8)
    return LinearLayout.LayoutParams(width, imgHeight)
}

fun startOTPTimer(onTimerTick: (String) -> Unit, onTimerFinished: () -> Unit): CountDownTimer? {
    return object : CountDownTimer(60000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            onTimerTick(
                    String.format(
                            "00 : %d",
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                            )
                    )
            )
        }

        override fun onFinish() {
            onTimerFinished()
        }
    }
}

fun AppBaseActivity.showProductDetail(model: ProductModel) {
    launchActivity<ProductDetailActivity> {
        putExtra(Constants.KeyIntent.DATA, model)
        if (isLoggedIn()) {
            if (getSharedPrefInstance().getStringValue(CART_DATA).checkIsEmpty()) {
                val keys = getCartList()
                keys.forEach {
                    if (model.id == it.product_id) {
                        putExtra(IS_ADDED_TO_CART, true)
                    }
                }
                return@launchActivity
            }
        }
    }
    addToRecentProduct(model)
}

fun Activity.sendCartCountChangeBroadcast() {
    sendBroadcast(CART_COUNT_CHANGE)
}


fun Activity.sendProfileUpdateBroadcast() {
    sendBroadcast(PROFILE_UPDATE)
}

fun Activity.sendWishlistBroadcast() {
    sendBroadcast(WISHLIST_UPDATE)
}

fun Activity.sendCartBroadcast() {
    sendBroadcast(CARTITEM_UPDATE)
}

fun Activity.registerCartReceiver(receiver: BroadcastReceiver) {
    registerBroadCastReceiver(CARTITEM_UPDATE, receiver)
}

fun Activity.registerCartCountChangeReceiver(receiver: BroadcastReceiver) {
    registerBroadCastReceiver(CART_COUNT_CHANGE, receiver)
}

fun Activity.registerProfileUpdateReceiver(receiver: BroadcastReceiver) {
    registerBroadCastReceiver(PROFILE_UPDATE, receiver)
}

fun Activity.registerWishListReceiver(receiver: BroadcastReceiver) {
    registerBroadCastReceiver(WISHLIST_UPDATE, receiver)
}

fun Activity.sendOrderCountChangeBroadcast() {
    sendBroadcast(ORDER_COUNT_CHANGE)
}

fun Activity.registerOrderCountChangeReceiver(receiver: BroadcastReceiver) {
    registerBroadCastReceiver(ORDER_COUNT_CHANGE, receiver)
}

fun Activity.sendBroadcast(action: String) {
    val intent = Intent()
    intent.action = action
    sendBroadcast(intent)
}

fun Activity.registerBroadCastReceiver(action: String, receiver: BroadcastReceiver) {
    val intent = IntentFilter()
    intent.addAction(action)
    registerReceiver(receiver, intent)
}


fun AppBaseActivity.registerUser(requestModel: RequestModel, isUpdate: Boolean) {
    getSharedPrefInstance().setValue(
            USER_DISPLAY_NAME,
            requestModel.firstName + " " + requestModel.lastName
    )
    getSharedPrefInstance().setValue(USER_EMAIL, requestModel.userEmail)
    getSharedPrefInstance().setValue(USER_FIRST_NAME, requestModel.firstName)
    getSharedPrefInstance().setValue(USER_LAST_NAME, requestModel.lastName)
    if (!isUpdate) {
        getSharedPrefInstance().setValue(USER_PASSWORD, requestModel.password)
        getSharedPrefInstance().setValue(USER_PROFILE, "")
    } else {
        if (requestModel.image != null) {
            getSharedPrefInstance().setValue(USER_PROFILE, requestModel.image)
        }
    }
    sendProfileUpdateBroadcast()
}

fun signIn(onResult: (Boolean) -> Unit) {
    getSharedPrefInstance().setValue(IS_LOGGED_IN, true)
    getSharedPrefInstance().setValue(USER_ID, Random().nextInt(10000).toString())
    onResult(true)

}


fun listAllProducts(onApiSuccess: (ArrayList<ProductModel>) -> Unit) {
    productsFromAssets(onApiSuccess = {
        onApiSuccess(it)
    })
}

fun subCategoryProducts(id: String, onApiSuccess: (ArrayList<ProductModel>) -> Unit) {
    productsFromAssets(onApiSuccess = {
        val list = ArrayList<ProductModel>()
        it.forEach { product ->
            product.categories.forEach { category ->
                if (category.name == id) {
                    list.add(product)
                }
            }
        }
        onApiSuccess(list)
    })

}
