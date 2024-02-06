package com.example.clicker.network.interceptors.responseCodeInterceptors

import java.io.IOException

class Authentication401Exception(message:String): IOException(message) {}