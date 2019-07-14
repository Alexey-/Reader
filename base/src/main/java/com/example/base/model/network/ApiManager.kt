package com.example.base.model.network

import android.net.TrafficStats
import com.example.base.core.MainSchedulers
import com.example.base.model.network.errors.ServerErrorInterceptor
import com.example.base.model.user.ISession
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ApiManager(
    private val baseUrl: String
) : IApiManager {

    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 15L
        private const val READ_TIMEOUT_SECONDS = 60L
    }

    override fun <T> createApi(apiClass: Class<T>, userSession: ISession?, gsonBuilder: GsonBuilder): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createClient(userSession))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(MainSchedulers.networkScheduler))
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .build()
            .create(apiClass)
    }

    private fun createClient(userSession: ISession?): OkHttpClient {
        val logging = HttpLoggingInterceptor { message ->
            Timber.tag("Network")
            Timber.d(message)
        }
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                TrafficStats.setThreadStatsTag(2000)
                chain.proceed(chain.request())
            }
            .addInterceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                if (userSession != null) {
                    builder.addHeader("Authorization", String.format("Bearer %s", userSession.token))
                }
                chain.proceed(builder.build())
            }
            .addInterceptor(ServerErrorInterceptor())
            .addInterceptor(logging)

        return builder.build()
    }

}