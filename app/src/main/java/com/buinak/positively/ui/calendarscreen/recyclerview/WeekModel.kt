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

package com.buinak.positively.ui.calendarscreen.recyclerview

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.buinak.positively.R
import com.buinak.positively.entities.DayEntry
import com.buinak.positively.entities.DayOfTheWeek
import com.buinak.positively.utils.Constants
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject

@EpoxyModelClass(layout = R.layout.activity_calendar_recycler_view_row)
abstract class WeekModel : EpoxyModelWithHolder<WeekModel.DateHolder>() {

    @EpoxyAttribute
    lateinit var contents: List<DayEntry>

    @EpoxyAttribute
    lateinit var updateSubject: Subject<DayEntry>

    @EpoxyAttribute
    var primaryMonth: Int = -1

    @EpoxyAttribute
    var secondaryMonth: Int = -1

    @EpoxyAttribute
    var primaryYear: Int = -1

    private var defaultTextSizeInSp = Constants.CALENDAR_ACTIVITY_DEFAULT_DATE_TEXT_SIZE_IN_SP
    private var selectedTextSizeInSp = Constants.CALENDAR_ACTIVITY_SELECTED_DATE_TEXT_SIZE_IN_SP

    private var disposable: Disposable? = null

    override fun bind(holder: WeekModel.DateHolder) {
        for (i in 0 until contents.size) {
            val entry = contents[i]
            val textView = holder.textViews[i]
            initialiseTextView(textView, entry)

            (textView.parent as FrameLayout).setOnClickListener {
                val date = textView.text.toString().toInt()
                val month = when (textView.alpha) {
                    1F -> primaryMonth
                    else -> secondaryMonth
                }
                val year = when (textView.alpha) {
                    1F -> primaryYear
                    else -> when (secondaryMonth) {
                        0 -> if (primaryMonth != 1) (primaryYear + 1) else primaryYear
                        11 -> if (primaryMonth != 10) (primaryYear - 1) else primaryYear
                        else -> primaryYear
                    }
                }
                //TODO: TEST CORRECT YEAR FOR JANUARY/DEC
                updateSubject.onNext(DayEntry(date, month, year))
            }
            disposable = updateSubject.subscribe { date ->
                holder.textViews.forEach {
                    it.background = null
                    it.setTypeface(null, Typeface.NORMAL)
                }

                //if the selected date belongs to this week model
                if (date.monthOfTheYear == primaryMonth || date.monthOfTheYear == secondaryMonth) {
                    //assign opacity to later check if the textview is correct
                    //for instance, if the date's month is the same as the primary month
                    //we need to make sure that the text view belongs to the primary month
                    val opacity: Float = when (date.monthOfTheYear) {
                        primaryMonth -> 1F
                        else -> 0.35F
                    }

                    val index = contents.indexOfFirst { it.dayOfTheMonth == date.dayOfTheMonth }
                    val selectedTextView = holder.textViews.getOrNull(index)
                    if (selectedTextView?.alpha == opacity) {
                        selectedTextView.setBackgroundResource(R.drawable.circle_drawable)
                        selectedTextView.setTypeface(null, Typeface.BOLD)
                    }
                }
            }
        }
    }

    private fun initialiseTextView(
        textView: TextView,
        entry: DayEntry
    ) {
        textView.text = entry.dayOfTheMonth.toString()
        if (entry.note.isNotEmpty()) {
            textView.setTextColor(
                when (DayOfTheWeek.valueOf(entry.dayOfTheWeek)) {
                    DayOfTheWeek.MONDAY -> textView.resources.getColor(R.color.mondayColor)
                    DayOfTheWeek.TUESDAY -> textView.resources.getColor(R.color.tuesdayColor)
                    DayOfTheWeek.WEDNESDAY -> textView.resources.getColor(R.color.wednesdayColor)
                    DayOfTheWeek.THURSDAY -> textView.resources.getColor(R.color.thursdayColor)
                    DayOfTheWeek.FRIDAY -> textView.resources.getColor(R.color.fridayColor)
                    DayOfTheWeek.SATURDAY -> textView.resources.getColor(R.color.saturdayColor)
                    DayOfTheWeek.SUNDAY -> textView.resources.getColor(R.color.sundayColor)
                }
            )
        } else {
            textView.setTextColor(Constants.DEFAULT_TEXT_GREY_COLOUR)
        }
        textView.alpha = when (entry.monthOfTheYear) {
            primaryMonth -> 1F
            else -> 0.35F
        }
        textView.setTypeface(null, Typeface.NORMAL)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSizeInSp)
    }

    override fun unbind(holder: DateHolder) {
        super.unbind(holder)
        disposable?.dispose()
    }


    inner class DateHolder : EpoxyHolder() {
        var textViews: ArrayList<TextView> = ArrayList()

        override fun bindView(itemView: View) {
            textViews.add(itemView.findViewById(R.id.textView_calendar_monday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_tuesday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_wednesday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_thursday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_friday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_saturday))
            textViews.add(itemView.findViewById(R.id.textView_calendar_sunday))
        }

    }


}