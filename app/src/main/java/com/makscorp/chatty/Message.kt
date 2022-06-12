package com.makscorp.chatty

class Message {
    var message: String? = null
    var senderId: String? = null
    var location: String? = null

    constructor() {

    }

    constructor(message: String?, senderId: String?, location: String?) {
        this.message = message
        this.senderId = senderId
        this.location = location
    }

}