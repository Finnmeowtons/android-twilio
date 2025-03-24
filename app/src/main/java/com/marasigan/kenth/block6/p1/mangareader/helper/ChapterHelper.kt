package com.marasigan.kenth.block6.p1.mangareader.helper

class ChapterHelper (){

    var chapterNumber: Int = 0
    var title: String = ""
    var imageUrls: List<String> = listOf()

    constructor(chapterNumber: Int, title: String, imageUrls: List<String>) : this() {
        this.chapterNumber = chapterNumber
        this.title = title
        this.imageUrls = imageUrls
    }

}