package com.example.mappoc

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun TextView.makeTextUnderLine() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

val uiScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

fun View.onOneClick(onClicked: () -> Unit) {
    this.setOnClickListener {
        onClicked.invoke()
    }
}