package com.example.clicker.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo

class CustomInputMethodService: InputMethodService() {

    override fun onInitializeInterface(){

    }
    override fun onBindInput(){

    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onCreateInputView(): View {
        return super.onCreateInputView()
    }

    override fun onCreateCandidatesView(): View {
        return super.onCreateCandidatesView()
    }

    override fun onCreateExtractTextView(): View {
        return super.onCreateExtractTextView()
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
    }
}
