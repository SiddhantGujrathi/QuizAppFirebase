package com.siddhant.quizapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siddhant.quizappnew.R
import com.siddhant.quizapp.models.TestResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TestResultAdapter(
    private val testResults: List<TestResult>,
    private val onDelete: (TestResult) -> Unit
) : RecyclerView.Adapter<TestResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val testTitle: TextView = view.findViewById(R.id.testTitle)
        val date: TextView = view.findViewById(R.id.date)
        val marksObtained: TextView = view.findViewById(R.id.marksObtained)
        val totalMarks: TextView = view.findViewById(R.id.totalMarks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val testResult = testResults[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault())
        val formattedDate = testResult.date?.let { timestamp ->
            val date = Date(timestamp.seconds * 1000)
            dateFormat.format(date)
        }

        holder.testTitle.text = testResult.testTitle
        holder.date.text = formattedDate
        holder.marksObtained.text = testResult.marksObtained
        holder.totalMarks.text = testResult.totalMarks

        holder.itemView.setOnLongClickListener {
            onDelete(testResult)
            true
        }
    }

    override fun getItemCount() = testResults.size
}
