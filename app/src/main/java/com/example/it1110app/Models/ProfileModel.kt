package com.example.it1110app.Models

class ProfileModel {
    lateinit var name : String
    lateinit var email : String
    var phone : String?
    var bookmarksCount : Int = 0

    constructor(name: String, email: String, phone: String?, bookmarksCount: Int) {
        this.name = name
        this.email = email
        this.phone = phone
        this.bookmarksCount = bookmarksCount
    }

}