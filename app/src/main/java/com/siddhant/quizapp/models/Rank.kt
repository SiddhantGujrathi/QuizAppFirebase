package com.siddhant.quizapp.models

import java.util.Date

data class Rank(
    val testTitle: String,
    val rank: Int,
    val email: String,
    val date: Date? = null, // Make sure timestamp field exists
    val marksObtained: String = "" // Ensure marksObtained is part of Rank
)