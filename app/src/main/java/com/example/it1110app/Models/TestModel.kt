package com.example.it1110app.Models

class TestModel {
    public var testId : String? = null
    public var topScore : Int = 0
    public var time : Int = 0
    public var completed : Boolean = false
    constructor(testId: String?, topScore: Int, time: Int) {
        this.testId = testId
        this.topScore = topScore
        this.time = time
    }


}