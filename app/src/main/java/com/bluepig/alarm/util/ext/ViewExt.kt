package com.bluepig.alarm.util.ext

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bluepig.alarm.R

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

fun ImageView.setThumbnail(data: Any) {
    val cornerRadius = resources.getDimension(R.dimen.base_spacing_1)
    load(data) {
        transformations(RoundedCornersTransformation(cornerRadius))
        crossfade(true)
        val errorDrawable =
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_image_place_holder
            ) ?: return
        val loadingDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_image_loading)
                ?: return
        placeholder(loadingDrawable)
        error(errorDrawable)
    }
}