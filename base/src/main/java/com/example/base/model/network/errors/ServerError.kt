package com.example.base.model.network.errors

import org.json.JSONObject

class ServerError {

    val code: ServerCode
    val httpCode: Int
    val message: String?
    val rawResponse: JSONObject?

    constructor(code: ServerCode) {
        this.code = code
        this.httpCode = 0
        this.message = null
        this.rawResponse = null

    }

    constructor(httpCode: Int, rawResponse: JSONObject) {
        this.httpCode = httpCode
        this.rawResponse = rawResponse
        if (httpCode in 200..299) {
            this.code = try {
                val responseIntCode = rawResponse.getInt("code")
                ServerCode.values().find { it.intCode == responseIntCode }!!
            } catch (e: Exception) {
                ServerCode.INTERNAL_ERROR
            }
            this.message = try {
                rawResponse.getString("message")
            } catch (e: Exception ) {
                null
            }
        } else {
            this.code = ServerCode.INTERNAL_ERROR
            this.message = null
        }
    }

    override fun toString(): String {
        return "ServerError(code=$code, httpCode=$httpCode, message=$message, rawResponse=$rawResponse)"
    }

}
