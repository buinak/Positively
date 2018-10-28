package com.buinak.positively.ui.mainscreen.recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.entities.plain.DayEntry

class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val idTextView = itemView.findViewById<TextView>(R.id.textView_id)
    val moodTextView = itemView.findViewById<TextView>(R.id.textView_mood)

    fun bindMood(dayEntry: DayEntry) {
        idTextView.text = dayEntry.id.slice(0..4)
        moodTextView.text = dayEntry.mood
    }
}