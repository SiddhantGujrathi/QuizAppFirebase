package com.siddhant.quizapp.models

import com.google.firebase.Timestamp

data class TestResult(
    var email: String = "",
    var testTitle: String = "",
    var date: Timestamp? = null ,
    var marksObtained: String = "",
    var totalMarks: String = "",
//    var timeTaken: Long = 0 // Time taken in milliseconds added for quiz model
)
