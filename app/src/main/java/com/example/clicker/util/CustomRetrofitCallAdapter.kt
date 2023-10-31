package com.example.clicker.util

import java.lang.reflect.Type
import retrofit2.CallAdapter
import retrofit2.Retrofit

class CustomRetrofitCallAdapter : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        TODO("Not yet implemented")
    }
}