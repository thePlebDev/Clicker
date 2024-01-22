package com.example.clicker.network.interceptors

import java.io.IOException

class NoNetworkException(message:String): IOException(message) {
}
