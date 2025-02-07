package com.example.clicker.presentation.selfStreaming

class RTMPNativeClient {


    companion object {
        init {
            System.loadLibrary("rtmp_client")
        }
    }



    external fun  nativeOpen( url:String, isPublishMode:Boolean, rtmpPointer:Long, sendTimeoutInMs:Int, receiveTimeoutInMs:Int):Int
}
