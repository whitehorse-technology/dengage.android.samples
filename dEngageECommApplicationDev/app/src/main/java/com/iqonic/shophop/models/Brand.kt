package com.iqonic.shophop.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Brand :Serializable{

    @SerializedName("brand_name")
    var brandName:String?=null

    @SerializedName("brand_color")
    var color:Int?=null

    @SerializedName("brand_id")
    var id:Int?=null

    var isSelected:Boolean?=false

}