package com.example.quizapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siddhant.quizappnew.R
import com.siddhant.quizapp.models.Rank
import java.text.SimpleDateFormat

class RankAdapter(private val rankList: List<Rank>) : RecyclerView.Adapter<RankAdapter.RankViewHolder>() {

    // ViewHolder class to hold the views
    inner class RankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankPosition: TextView = itemView.findViewById(R.id.rankPosition)
        val rankEmail: TextView = itemView.findViewById(R.id.rankEmail)
        val rankMarks: TextView = itemView.findViewById(R.id.rankMarks)
        val timeSubmitted: TextView = itemView.findViewById(R.id.timeTestSubmitted)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rank_list_item, parent, false)
        return RankViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        val rank = rankList[position]
        holder.rankPosition.text = "Rank ${rank.rank}"
        holder.rankEmail.text = rank.email
        holder.rankMarks.text = rank.marksObtained
        val dateFormat = SimpleDateFormat("hh:mm a")
        val formattedDate = rank.date?.let { dateFormat.format(it) }

        holder.timeSubmitted.text = formattedDate.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return rankList.size
    }
}
