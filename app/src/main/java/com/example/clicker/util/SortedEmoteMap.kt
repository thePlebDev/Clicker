package com.example.clicker.util

class SortedEmoteMap {

    fun hashCodeCyclicShift(s:String):Int{
        var h = 0
        for (i in s.indices) {
            h = (h shl 5) or (h ushr 27) // 5-bit cyclic shift of the running sum
            h += s[i].code // add in next character
        }
        return h
    }
}

