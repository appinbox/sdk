package com.appinbox.sdk.svc;

import com.appinbox.sdk.auth.Device;
import com.appinbox.sdk.auth.User;
import com.appinbox.sdk.msg.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MsgApi {
    @POST("/v1/devices")
    Call<Device> registerDevice(@Header("x-ai-app") String appId, @Header("x-ai-key") String appKey, @Header("x-ai-contact") String contact, @Body Device device);

    @DELETE("/v1/devices/{deviceId}")
    Call<Device> clearDevice(@Header("x-ai-app") String appId, @Header("x-ai-key") String appKey, @Header("x-ai-contact") String contact, @Path("deviceId") String deviceId);

    @GET("/v1/messages")
    Call<List<Message>> getMessages(@Header("x-ai-app") String appId, @Header("x-ai-key") String appKey, @Header("x-ai-contact") String contact);

    @GET("/v1/messages/{id}")
    Call<Message> getMessage(@Header("x-ai-app") String appId, @Header("x-ai-key") String appKey, @Header("x-ai-contact") String contact, @Path("id") String msgId);

    @POST("/v1/messages/{id}/read")
    Call<Message> readMessage(@Header("x-ai-app") String appId, @Header("x-ai-key") String appKey, @Header("x-ai-contact") String contact, @Path("id") String msgId);
}
