package com.example.it1110app.Models

class RankModel {
    var score : Int = 0
    var rank : Int = 0
    var name : String = ""

    constructor(score: Int, rank: Int, name: String) {
        this.score = score
        this.rank = rank
        this.name = name
    }
}