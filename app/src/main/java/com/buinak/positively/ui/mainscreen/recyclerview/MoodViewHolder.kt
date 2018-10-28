/*
 * Copyright 2018 Konstantin Buinak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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