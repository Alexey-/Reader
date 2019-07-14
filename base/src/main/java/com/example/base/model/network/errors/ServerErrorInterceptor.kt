package com.example.base.model.network.errors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import org.json.JSONObject

import java.io.IOException
import java.nio.charset.Charset

class ServerErrorInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response

        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            throw ServerErrorException(ServerError(ServerCode.NETWORK_ERROR))
        }

        val responseCode = response.code()
        if (responseCode == 200) {
            return response
        }

        val errorJson: JSONObject
        try {
            val responseBody = parseResponseBody(response.body())
            errorJson = JSONObject(responseBody)
        } catch (e: Exception) {
            throw ServerErrorException(ServerError(ServerCode.UNKNOWN_CODE))
        }

        throw ServerErrorException(ServerError(responseCode, errorJson))
    }

    @Throws(IOException::class)
    private fun parseResponseBody(body: ResponseBody?): String {
        body!!.source().use { source ->
            source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer()
            return buffer.clone().readString(Charset.forName("UTF-8"))
        }
    }

}
