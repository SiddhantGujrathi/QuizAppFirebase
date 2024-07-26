package com.siddhant.quizapp.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.siddhant.quizapp.models.Quiz
import com.siddhant.quizapp.models.TestResult
import com.siddhant.quizappnew.databinding.ActivityResultBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {

    private var score: Int = 0
    private var total: Int = 0
    private lateinit var binding: ActivityResultBinding

    lateinit var quiz: Quiz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        calculateScore()
        setAnswerView()
        storeSubmittedData(quiz)
    }

    @OptIn(UnstableApi::class)
    private fun storeSubmittedData(quiz: Quiz) {
        lateinit var firebaseAuth: FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val email = firebaseAuth.currentUser?.email
        val testTitle = quiz.title
        val scoredMarks = "$score"
        var totalMarks = "$total"


        val testResult = TestResult(email.toString(), testTitle , Timestamp.now(), scoredMarks, totalMarks)


        db.collection("users").add(testResult)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }



    }

    private fun setAnswerView() {
        val builder = StringBuilder("")
        var questionNumber = 1
        for (entry in quiz.questions.entries) {
            val question = entry.value
            if(question.userAnswer.length == 0){
                question.userAnswer = " Not Selected "
            }
            builder.append("<br/><br/><font color'#18206F'><b>${questionNumber}. Question: ${question.description}</b></font><br/><br/>")
            builder.append("<font color='#00EA10'>Correct Answer: ${question.answer}</font>")
            if(question.answer != question.userAnswer)
                builder.append("<font color='#ff0000'><br/>Your Answer: ${question.userAnswer}</font>")
            questionNumber++
            builder.append("<br/><hr/>")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.txtAnswer.text = Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_COMPACT);
        } else {
            binding.txtAnswer.text = Html.fromHtml(builder.toString())
        }

    }

    private fun calculateScore() {
        score = 0
        total = 0
        for (entry in quiz.questions.entries) {
            val question = entry.value
            total += 10
            if (question.answer == question.userAnswer) {
                score += 10
            }
        }
        binding.txtScore.text = "Your Score : $score / $total "
        var right = score/10;
        var wrong = total/10 - right ;
        binding.txtWrongAnswer.text = "Wrong : $wrong"
        binding.txtCorrectAnswer.text = "Right : $right"
    }
}