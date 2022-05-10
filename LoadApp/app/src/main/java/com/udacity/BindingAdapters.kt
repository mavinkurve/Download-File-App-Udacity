package com.udacity

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("statusText")
fun bindStatusText(view: TextView, status: String){
    when(status){
        "Failed" -> {
            view.text = "Failed"
            view.setTextColor(Color.RED)
        }
        "Success" -> {
            view.text = "Success"
            view.setTextColor(Color.GREEN)
        }
        else -> {
            view.text = "Unavailable"
            view.setTextColor(Color.GRAY)
        }
    }
}