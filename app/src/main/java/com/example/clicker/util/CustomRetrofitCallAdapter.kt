package com.example.clicker.util

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class CustomRetrofitCallAdapter: CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        TODO("Not yet implemented")
    }

}