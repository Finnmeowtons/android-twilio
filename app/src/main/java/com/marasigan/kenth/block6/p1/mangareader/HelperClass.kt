package com.marasigan.kenth.block6.p1.mangareader

class HelperClass() {
    var userId: String = ""  // Unique Firebase Key
    var username: String = ""
    var email: String = ""
    var password: String = ""

    constructor(userId: String, username: String, email: String, password: String) : this() {
        this.userId = userId
        this.username = username
        this.email = email
        this.password = password
    }
}
