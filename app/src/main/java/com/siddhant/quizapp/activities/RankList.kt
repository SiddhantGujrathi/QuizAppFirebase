package com.siddhant.quizapp.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.adapters.RankAdapter
import com.example.quizapp.models.Rank
import com.example.quizappnew.databinding.ActivityRankListBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RankList : AppCompatActivity() {

    private lateinit var binding: ActivityRankListBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var rankAdapter: RankAdapter
    private val rankList = mutableListOf<Rank>()
    private val rankMap = HashMap<String, MutableList<Rank>>() // Map to hold ranks per testTitle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRankListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()


        rankAdapter = RankAdapter(rankList)
        binding.testSubmittedRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.testSubmittedRecyclerView.adapter = rankAdapter

        binding.dateNow.text = getCurrentDateFormatted()

        fetchAndLogRanks(getCurrentDateFormatted())

        rankAdapter.notifyDataSetChanged()
    }


    // Function to get the current date in "dd MMM yyyy" format like 21 Jul 2024
    fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }


    /* For Unique Gmails */
    private fun fetchAndLogRanks(testTitle: String) {
        firestore.collection("users")
            .whereEqualTo("testTitle", testTitle)  // Filter by testTitle
            .get()
            .addOnSuccessListener { result ->
                if(result.size() == 0 ){
                    rankList.add(Rank(testTitle,0,"-",null,"-"))
                }
                val uniqueRanksMap = HashMap<String, Rank>()  // Map to store highest marks per email

                for (document in result) {
                    val email = document.getString("email") ?: ""
                    val marksObtained = document.getString("marksObtained") ?: "0"
                    val date = document.getTimestamp("date")?.toDate() // Convert Timestamp to Date

                    // Convert marksObtained to an integer for comparison
                    val marksInt = marksObtained.toIntOrNull() ?: 0

                    // Check if the email is already in the map
                    val existingRank = uniqueRanksMap[email]

                    // If not in the map or has higher marks or same marks but more recent date, update the map
                    if (existingRank == null || marksInt > existingRank.marksObtained.toIntOrNull() ?: 0 ||
                        (marksInt == existingRank.marksObtained.toIntOrNull() ?: 0 && date?.after(existingRank.date) == true)
                    ) {
                        uniqueRanksMap[email] = Rank(testTitle, 0, email, date, marksObtained)
                    }
                }

                // Convert map values to a list for sorting and display
                val uniqueRanks = uniqueRanksMap.values.toList()

                // Sort the list first by marksObtained (descending), then by date (descending)
                val sortedRanks = uniqueRanks.sortedWith(compareByDescending<Rank> { it.marksObtained.toIntOrNull() ?: 0 }.thenByDescending { it.date })

                // Assign ranks
                sortedRanks.forEachIndexed { index, rank ->
                    rankList.add(rank.copy(rank = index + 1)) // Set rank based on position in sorted list
                }

                // Notify the adapter about data change
                rankAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Log.e("RankList", "Error fetching data: ${exception.message}")
            }
    }
}