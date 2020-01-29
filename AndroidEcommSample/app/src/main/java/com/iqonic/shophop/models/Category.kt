package com.iqonic.shophop.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Category :Serializable{

    @SerializedName("category_name")
    var categoryName:String?=null

}

data class CategoryData(
        val count: Int,
        val description: String,
        val id: Int,
        val image: Any,
        val menu_order: Int,
        var name: String,
        val parent: Int,
        val slug: String,
        var isSelected: Boolean

) :  Serializable