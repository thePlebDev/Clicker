package com.example.clicker.keyboard

import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.example.clicker.util.AndroidVersion

class ModerzImeService {

    private val inputMethodService = CustomInputMethodService()


    fun currentInputConnection(): InputConnection? {
        return inputMethodService.currentInputConnection
    }

//    fun inputFeedbackController(): InputFeedbackController? {
//        return inputMethodService.inputFeedbackController
//    }

    fun showUi() {
        val ims = inputMethodService
        if (AndroidVersion.ATLEAST_API28_P) {
            ims.requestShowSelf(0)
        }
    }

    fun hideUi() {
        val ims = inputMethodService
        if (AndroidVersion.ATLEAST_API28_P) {
            ims.requestHideSelf(0)
        }
    }
}