package com.appinbox.sdk.svc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.appinbox.sdk.AppInboxSDK.SDK_VERSION;

public class ApiBuilder {
    private static MsgApi api;
    private static Retrofit retrofit;
    private ApiBuilder() {

    }
    public static MsgApi getInstance() {
        if(retrofit == null) {
            int timeout = 60;
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("X-Request-ID", UUID.randomUUID().toString())
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                                .header("Cache-Control", "no-cache")
                                .header("X-User-Agent", "android/" + SDK_VERSION)
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(logging)
                    .build();

            final GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                try {
                    return new Date(json.getAsLong() * 1000);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    return null;
                }
            });
            Gson gson = builder.create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.theappinbox.com")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
        }

        if(api == null) {
            api = retrofit.create(MsgApi.class);
        }

        return api;
    }
}
