package com.example.base.model.network.responses

import com.google.gson.annotations.SerializedName

open class ListResponse<T>(
    @SerializedName(value = "list", alternate = ["items"]) val items: List<T>
) : GenericResponse()
