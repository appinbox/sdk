package com.appinbox.sdk.svc

import com.appinbox.sdk.model.Device
import com.appinbox.sdk.model.Message
import retrofit2.Call
import retrofit2.http.*

interface AppInboxApi {
        @POST("/v1/devices")
        fun registerDevice(
            @Header("x-ai-app") appId: String?,
            @Header("x-ai-key") appKey: String?,
            @Header("x-ai-contact") contact: String?,
            @Body device: Device?
        ): Call<Device>

        @DELETE("/v1/devices/{deviceId}")
        fun clearDevice(
            @Header("x-ai-app") appId: String?,
            @Header("x-ai-key") appKey: String?,
            @Header("x-ai-contact") contact: String?,
            @Path("deviceId") deviceId: String?
        ): Call<Device>

        @GET("/v1/messages")
        fun getMessages(
            @Header("x-ai-app") appId: String?,
            @Header("x-ai-key") appKey: String?,
            @Header("x-ai-contact") contact: String?
        ): Call<List<Message>>

        @GET("/v1/messages/{id}")
        fun getMessage(
            @Header("x-ai-app") appId: String?,
            @Header("x-ai-key") appKey: String?,
            @Header("x-ai-contact") contact: String?,
            @Path("id") msgId: String?
        ): Call<Message>

        @POST("/v1/messages/{id}/read")
        fun readMessage(
            @Header("x-ai-app") appId: String?,
            @Header("x-ai-key") appKey: String?,
            @Header("x-ai-contact") contact: String?,
            @Path("id") msgId: String?
        ): Call<Message>
}