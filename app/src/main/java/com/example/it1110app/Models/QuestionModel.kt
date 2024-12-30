package com.example.it1110app.Models

class QuestionModel {
    public var qId : String? = null
    public var question : String? = null
    public var optionA : String? = null
    public var optionB : String? = null
    public var optionC : String? = null
    public var optionD : String? = null
    public var correctAns : Int? = null
    public var selectedAns : Int? = null
    public var status : Int = 0
    public var isBookmarked : Boolean = false

    constructor(qId: String?, question: String?, optionA: String?, optionB: String?, optionC: String?, optionD: String?, correctAns: Int?, selectedAns: Int?, status : Int, isBookmarked : Boolean) {
        this.qId = qId
        this.question = question
        this.optionA = optionA
        this.optionB = optionB!!
        this.optionC = optionC!!
        this.optionD = optionD!!
        this.correctAns = correctAns
        this.selectedAns = selectedAns
        this.status = status
        this.isBookmarked = isBookmarked
    }
}