package com.iqonic.shophop.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SubCategory :Serializable{

    @SerializedName("subcategory_name")
    var categoryName:String?=null

    var isSelected:Boolean?=false

    @SerializedName("category")
    var id: Int? = null

}