package com.example.base.model.network.responses

import com.google.gson.annotations.SerializedName

open class ObjectResponse<T>(
    @SerializedName(value = "object", alternate = ["data"]) val data: T
) : GenericResponse()
