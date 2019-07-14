package com.example.base.model.network.responses

import com.example.base.model.network.errors.ServerCode
import com.google.gson.annotations.SerializedName

open class GenericResponse(
    @SerializedName("code") var code: ServerCode = ServerCode.UNKNOWN_CODE
)
