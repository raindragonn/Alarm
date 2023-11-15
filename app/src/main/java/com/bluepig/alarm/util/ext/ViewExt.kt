package com.bluepig.alarm.util.ext

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)


fun View.hideKeyboard() {
    val imm = context.inputMethodManager
    imm.hideSoftInputFromWindow(applicationWindowToken, 0)
}

fun EditText.setOnEnterListener(action: (String) -> Unit) {
    setOnKeyListener { view, keyCode, event ->
        if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action.invoke(text.toString())
            view.hideKeyboard()
            return@setOnKeyListener true
        } else {
            return@setOnKeyListener false
        }
    }
}


fun RecyclerView.setOnLoadMore(triggerLessCount: Int = 5, action: () -> Unit) {
    val lm = layoutManager as? LinearLayoutManager ?: return
    clearOnScrollListeners()
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val itemCount = lm.itemCount

            if (lm.findLastCompletelyVisibleItemPosition() >= itemCount - triggerLessCount) {
                action()
            }
        }
    }
    addOnScrollListener(scrollListener)
}