package com.makscorp.chatty

class Message {
    var message: String? = null
    var senderId: String? = null
    var latitude: Double? = null
    var longitude: Double? = null

    constructor() {

    }

    constructor(message: String?, senderId: String?, latitude: Double?, longitude: Double?) {
        this.message = message
        this.senderId = senderId
        this.latitude = latitude
        this.longitude = longitude
    }
}