package com.siddhant.quizapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.quizapp.adapters.TestResultAdapter
import com.example.quizapp.models.TestResult
import com.example.quizappnew.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestResultAdapter
    private val testResults = mutableListOf<TestResult>()
    private lateinit var txtEmail: TextView
    private lateinit var btnlogout: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        txtEmail = this.findViewById(R.id.txtEmail)
        btnlogout = findViewById(R.id.btnLogout)
        recyclerView = findViewById(R.id.testSubmittedRecyclerView)

        firebaseAuth = FirebaseAuth.getInstance()

        txtEmail.text = firebaseAuth.currentUser?.email

        fetchTestResults(firebaseAuth.currentUser?.email.toString())

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TestResultAdapter(testResults) { testResult ->
            showDeleteConfirmationDialog(testResult)
        }
        recyclerView.adapter = adapter

        /* To play animation */
        val animationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)
        animationView.setAnimation(R.raw.apti_animation)
        animationView.playAnimation()

        btnlogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchTestResults(email: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                testResults.clear()
                for (document in result) {
                    val testResult = document.toObject(TestResult::class.java)
                    testResults.add(testResult)
                }
                // Sort testResults by date in descending order
                testResults.sortByDescending { it.date?.seconds }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("TestResultsActivity", "Error getting test results", e)
            }
    }

    private fun showDeleteConfirmationDialog(testResult: TestResult) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_confirmation, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<AppCompatButton>(R.id.btnYes).setOnClickListener {
            deleteTestResult(testResult)
            alertDialog.dismiss()
        }

        dialogView.findViewById<AppCompatButton>(R.id.btnNo).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun deleteTestResult(testResult: TestResult) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", testResult.email)
            .whereEqualTo("testTitle", testResult.testTitle)
            .whereEqualTo("date", testResult.date)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("ProfileActivity", "DocumentSnapshot successfully deleted!")
                            fetchTestResults(firebaseAuth.currentUser?.email.toString())
                        }
                        .addOnFailureListener { e ->
                            Log.w("ProfileActivity", "Error deleting document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("ProfileActivity", "Error finding document to delete", e)
            }
    }
}
