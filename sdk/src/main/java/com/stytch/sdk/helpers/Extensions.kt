package com.stytch.sdk.helpers

import android.content.Context
import android.content.res.Resources
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

val Number.px: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density

val Number.dp: Float
    get() = this.toFloat() * Resources.getSystem().displayMetrics.density

fun Fragment.hideKeyboard() {
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}
