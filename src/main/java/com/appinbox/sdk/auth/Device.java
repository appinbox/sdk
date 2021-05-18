package com.appinbox.sdk.auth;

import com.appinbox.sdk.AppInboxSDK;

public class Device {
    private String id;
    private String sdkVersion;

    public Device(String id) {
        this.id = id;
        this.sdkVersion = AppInboxSDK.SDK_VERSION;
    }
}
