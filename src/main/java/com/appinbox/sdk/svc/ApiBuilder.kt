package com.appinbox.sdk.svc

import com.appinbox.sdk.AppInboxSDK
import com.google.gson.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

object ApiBuilder {
    var api: AppInboxApi? = null
        get() {
            init()
            if (field == null) {
                field = retrofit!!.create(AppInboxApi::class.java)
            }
            return field
        }
        private set
    private var retrofit: Retrofit? = null
    private fun init() {
        if (retrofit == null) {
            val timeout = 60
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .writeTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("X-Request-ID", UUID.randomUUID().toString())
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Cache-Control", "no-cache")
                        .header("X-User-Agent", "android/" + AppInboxSDK.SDK_VERSION)
                        .method(original.method, original.body)
                        .build()
                    chain.proceed(request)
                })
                .addInterceptor(logging)
                .build()
            val builder = GsonBuilder()
            builder.registerTypeAdapter(
                Date::class.java,
                JsonDeserializer<Date> { json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext? ->
                    try {
                        return@JsonDeserializer Date(json.asLong * 1000)
                    } catch (e: JsonParseException) {
                        e.printStackTrace()
                        return@JsonDeserializer null
                    }
                })
            val gson = builder.create()
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.theappinbox.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build()
        }
    }

    @JvmName("getApi1")
    fun getApi(): AppInboxApi {
        return api!!
    }
}