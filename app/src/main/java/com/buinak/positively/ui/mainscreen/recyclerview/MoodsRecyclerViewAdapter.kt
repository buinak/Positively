package com.buinak.positively.ui.mainscreen.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buinak.positively.R
import com.buinak.positively.entities.plain.DayEntry

class MoodsRecyclerViewAdapter(var moods: List<DayEntry>) : RecyclerView.Adapter<MoodViewHolder>() {
    override fun getItemCount(): Int = moods.size

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bindMood(moods[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_main_recycler_view_row, parent, false)
        return MoodViewHolder(view)
    }
}