package com.syrous.expensetracker.widget

import android.util.Log
import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

class RootViewDeferringInsetsCallback(
    private val persistentInsetTypes: Int,
    private val deferredInsetTypes: Int
) : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE),
    OnApplyWindowInsetsListener {

    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsetsCompat.Type values"
        }
    }

    private var view: View? = null
    private var lastWindowInsets: WindowInsetsCompat? = null

    private var deferredInsets = false
    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: MutableList<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        TODO("Not yet implemented")
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat?): WindowInsetsCompat {

        view = v
        lastWindowInsets = insets

        val types = when {
            deferredInsets -> persistentInsetTypes
            else -> persistentInsetTypes or deferredInsetTypes
        }
        val typeInsets = insets?.getInsets(types)!!
        Log.d("AddActivity", "insets $typeInsets")
        v?.setPadding(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom)
        return WindowInsetsCompat.CONSUMED
    }
}