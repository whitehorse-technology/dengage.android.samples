package com.iqonic.shophop.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Color :Serializable{

    @SerializedName("color_name")
    var colorName:Int?=null

    var isSelected:Boolean?=false

    var color: String? = null
    var id: Int? = null



}