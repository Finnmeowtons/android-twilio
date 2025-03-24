package com.marasigan.kenth.block6.p1.mangareader.helper

class MangaHelper (){
    var id: String = ""
    var title: String = ""
    var description: String = ""
    var imageUrl: String = ""

    constructor(id: String, title: String, description: String, imageUrl: String) : this() {
        this.id = id
        this.title = title
        this.description = description
        this.imageUrl = imageUrl
    }

}