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

package com.buinak.positively.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.cardview.widget.CardView


object ViewUtils {

    fun animateViewColourChange(view: View, colourFrom: Int, colourTo: Int, duration: Int) {
        val backgroundColorAnimator = ObjectAnimator.ofObject(
            view,
            "backgroundColor",
            ArgbEvaluator(),
            colourFrom,
            colourTo
        )
        backgroundColorAnimator.duration = duration.toLong()
        backgroundColorAnimator.start()
    }

    fun animateWindowColourChange(window: Window, colourTo: Int, duration: Int) {
        val colourFrom = window.statusBarColor
        val backgroundColorAnimator = ObjectAnimator.ofObject(
            window,
            "statusBarColor",
            ArgbEvaluator(),
            colourFrom,
            colourTo
        )
        backgroundColorAnimator.duration = duration.toLong()
        backgroundColorAnimator.start()
    }

    fun animateTextColourChange(view: TextView, colourTo: Int, duration: Int) {
        val colourFrom = view.currentTextColor
        val backgroundColorAnimator = ObjectAnimator.ofObject(
            view,
            "textColor",
            ArgbEvaluator(),
            colourFrom,
            colourTo
        )
        backgroundColorAnimator.duration = duration.toLong()
        backgroundColorAnimator.start()
    }

    fun animateCardViewColourChange(view: CardView, colourFrom: Int, colourTo: Int, duration: Int) {
        val backgroundColorAnimator = ObjectAnimator.ofObject(
            view,
            "cardBackgroundColor",
            ArgbEvaluator(),
            colourFrom,
            colourTo
        )
        backgroundColorAnimator.duration = duration.toLong()
        backgroundColorAnimator.start()
    }

    //dynamically outputs the needed textsize to fit all the text in the desired width
    fun correctTextSize(textView: TextView, desiredWidth: Int, sizingUp: Boolean) {
        val paint = Paint()
        val bounds = Rect()

        paint.typeface = textView.typeface
        var textSize: Float
        if (sizingUp) {
            textSize = 100f
        } else {
            textSize = textView.textSize
        }
        paint.textSize = textSize
        val text = textView.text.toString()
        paint.getTextBounds(text, 0, text.length, bounds)

        while (bounds.width() > desiredWidth) {
            textSize--
            paint.textSize = textSize
            paint.getTextBounds(text, 0, text.length, bounds)
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    }
}