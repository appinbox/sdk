package com.appinbox.sdk.model

import java.util.*

data class Message (
    var id: String = "",
    var title: String = "",
    var body: String = "",
    var sentAt: Date = Date(),
    var readAt: Date? = null
)
