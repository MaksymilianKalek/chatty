package com.makscorp.chatty

class Message {
    var message: String? = null
    var senderId: String? = null
    var location: String? = null
    var latitude: Double? = null
    var longitude: Double? = null


    constructor() {

    }

    constructor(
        message: String?,
        senderId: String?,
        location: String?,
        latitude: Double?,
        longitude: Double?
    ) {
        this.message = message
        this.senderId = senderId
        this.location = location
        this.longitude = longitude
        this.latitude = latitude
    }


}