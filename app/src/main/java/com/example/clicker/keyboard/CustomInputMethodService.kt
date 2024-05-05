package com.example.clicker.keyboard

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo

class CustomInputMethodService: InputMethodService() {

    override fun onInitializeInterface(){
        Log.d("CustomInputMethodService","onInitializeInterface")

    }
    override fun onBindInput(){
        Log.d("CustomInputMethodService","onBindInput")

    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d("CustomInputMethodService","onStartInput")
    }

    override fun onCreateInputView(): View {
        Log.d("CustomInputMethodService","onCreateInputView")
        return super.onCreateInputView()
    }

    override fun onCreateCandidatesView(): View {
        Log.d("CustomInputMethodService","onCreateCandidatesView")
        return super.onCreateCandidatesView()
    }

    override fun onCreateExtractTextView(): View {
        Log.d("CustomInputMethodService","onCreateExtractTextView")
        return super.onCreateExtractTextView()
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        Log.d("CustomInputMethodService","onStartInputView")
        super.onStartInputView(editorInfo, restarting)
    }
}
