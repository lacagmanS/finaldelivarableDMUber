package com.example.dmuber


class Admin {
    @JvmField
    var email: String? = null

    @JvmField
    var fullName: String? = null

    @JvmField
    var phoneNumber: String? = null

    @JvmField
    var userName: String? = null

    constructor() {
        // Default constructor is necessary for Firebase
    }

    constructor(email: String?, fullName: String?, phoneNumber: String?, userName: String?) {
        this.email = email
        this.fullName = fullName
        this.phoneNumber = phoneNumber
        this.userName = userName
    }
}