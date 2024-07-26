package com.siddhant.quizapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.siddhant.quizapp.adapters.OptionAdapter
import com.siddhant.quizapp.models.Question
import com.siddhant.quizapp.models.Quiz
import com.siddhant.quizappnew.databinding.ActivityQuestionBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class QuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBinding

    var quizzes : MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? = null
    var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Fetching data from Firebase and storing to quizzes and questions */
        setUpFirestore()

        /* Takes care of next , prev , submit button visibility and intent passing when clicked on submit button */
        setUpEventListener()
    }

    private fun setUpEventListener() {
        binding.btnPrevious.setOnClickListener {
            index--
            bindViews()
        }

        binding.btnNext.setOnClickListener {
            index++
            bindViews()
        }

        /* On submit sends the json file to the result activity and opens the ResultActivity*/
        binding.btnSubmit.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            val json  = Gson().toJson(quizzes!![0])
            intent.putExtra("QUIZ", json)
            finish()
            startActivity(intent)
        }
    }

    private fun setUpFirestore() {
        /*To get instance of Firestore Firebase */
        val firestore = FirebaseFirestore.getInstance()

        /* Here we are getting value from previous activity which may be from recyclerView of MainActivity OR datePicker */
        var date = intent.getStringExtra("DATE")

        if (date != null) {
            /* We are putitng filter in collection where title = "date" */
            firestore.collection("quizes").whereEqualTo("title", date)
                .get()
                .addOnSuccessListener {

                    if(it != null && !it.isEmpty){
                        /*Fetching id,title,questions in Quiz */
                        quizzes = it.toObjects(Quiz::class.java)

                        /*Fetching each questions description,option1,option2,option3,option4,answer into questions */
                        questions = quizzes!![0].questions
                        bindViews()
                    }
                }
        }
    }


    private fun bindViews() {
        binding.btnPrevious.visibility = View.GONE
        binding.btnSubmit.visibility = View.GONE
        binding.btnNext.visibility = View.GONE

        if(index == 1){ //first question
            binding.btnNext.visibility = View.VISIBLE
        }
        else if(index == questions!!.size) { // last question
            binding.btnSubmit.visibility = View.VISIBLE
            binding.btnPrevious.visibility = View.VISIBLE
        }
        else{ // Middle
            binding.btnPrevious.visibility = View.VISIBLE
            binding.btnNext.visibility = View.VISIBLE
        }

        /* Iterating over questions fetched from database and setting to the screen */
        val question = questions!!["question$index"]
        question?.let {
            binding.description.text = it.description
            val optionAdapter = OptionAdapter(this, it)
            binding.optionList.layoutManager = LinearLayoutManager(this)
            binding.optionList.adapter = optionAdapter
            binding.optionList.setHasFixedSize(true)
        }
    }
}


