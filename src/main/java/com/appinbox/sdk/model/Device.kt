package com.appinbox.sdk.model;

import com.appinbox.sdk.AppInboxSDK;

data class Device (var id: String = "", val sdkVersion: String = AppInboxSDK.SDK_VERSION) {
    constructor(deviceId: String) : this() {
        id = deviceId
    }
}