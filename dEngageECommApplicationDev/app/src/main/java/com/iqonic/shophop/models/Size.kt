package com.iqonic.shophop.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Size :Serializable{

    @SerializedName("size_name")
    var sizeName:String?=null

    var isSelected:Boolean?=false

    var size: String? = null

    var id: Int? = null


}