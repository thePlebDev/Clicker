package com.example.clicker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.FileProvider
import com.example.clicker.BuildConfig
import com.example.clicker.R
import java.io.File
import java.io.IOException
import java.util.Objects


class ScreenRecordingService : Service() {

    private val CHANNEL_ID = "001"
    var channelName = "RecordChannel"
    private var startCommendId = 0;
    private lateinit var mMediaRecorder: MediaRecorder
    private lateinit var mMediaProjection:MediaProjection
    private var mResultCode = 0
    private lateinit var mResultData: Intent
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mScreenDensity = 0
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mFilePath = ""




    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startCommendId = startId

        when(intent?.action){
            BackgroundStreamService.Actions.START.toString()->{
                val notification2 = NotificationCompat.Builder(this, "CHANNEL_ID").build()

                //request that the service run in the foreground
                ServiceCompat.startForeground(
                    this,
                    99,
                    notification2,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
                mResultCode = intent.getIntExtra("code", -1);
                mResultData = intent.getParcelableExtra("data")!!;

                mFilePath= intent.getStringExtra("output_file_path")?:""
                mScreenWidth = intent.getIntExtra("width", 0);
                mScreenHeight = intent.getIntExtra("height", 0);
                mScreenDensity = intent.getIntExtra("density", 1);
                Log.d("SCREETHEIGHTANDWIDTHtESTING","mScreenWidth-->$mScreenWidth")
                Log.d("SCREETHEIGHTANDWIDTHtESTING","mScreenHeight-->$mScreenHeight")


                initRecorder()
                initMediaProjection()
                initVirtualDisplay() //this might be causing the problem

                //Start Recording
              //  mMediaRecorder.start();
            }//END OF THE START FUNCTION
            BackgroundStreamService.Actions.END.toString()->{
                //todo:I need to stop the recording
                //STOP Recording
//                stopRecording()
                stopSelf()
            }
        }


        return super.onStartCommand(intent, flags, startId)
        //todo: THIS IS WHERE THE SERVICE SHOULD START


    }



    @RequiresApi(Build.VERSION_CODES.S)
    fun initRecorder() {
        mMediaRecorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)  // Set the audio source
            setVideoSource(MediaRecorder.VideoSource.SURFACE);
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // Set output format
            setOutputFile(mFilePath) // Set the output file path
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // Set audio encoder
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)

            setVideoSize(mScreenWidth, mScreenHeight)  // Adjust to a higher resolution if needed

            // Set bitrate (increase for better quality)
            setVideoEncodingBitRate(8000000)

            try {
                prepare() // Prepare the recorder
                Log.e("initRecorderTest", "prepare() called")
                start()  // Start recording
            } catch (e: IOException) {
                Log.e("initRecorderTest", "prepare() failed: ${e.message}")
            }
        }
    }

    //TODO: mResultCode AND mResultData NEED TO BE PASSED FROM THE FRAGMENT
    private fun initMediaProjection() {
        //The token is represented by an instance of the MediaProjection class.
        mMediaProjection =
            (Objects.requireNonNull(getSystemService(MEDIA_PROJECTION_SERVICE)) as MediaProjectionManager).getMediaProjection(
                mResultCode,
                mResultData
            )

        val handler = Handler(Looper.getMainLooper())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            mMediaProjection.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    super.onStop()
                }
            }, handler)
        } else {
            mMediaProjection.registerCallback(object : MediaProjection.Callback() { // Nothing
                // We don't use it but register it to avoid runtime error from SDK 34+.
            }, handler)
        }
    }

    private fun initVirtualDisplay() {
        if (mMediaProjection == null) {
            Log.d("ScreenCaptureTesting", "initVirtualDisplay: " + " Media projection is not initialized properly.")
            return
        }
        Log.d("initRecorderTest", "mMediaProjection.createVirtualDisplay()")
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(
            "ScreenCapture",
            mScreenWidth,
            mScreenHeight,
            mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mMediaRecorder.getSurface(),
            null,
            null
        )
    }



    override fun onDestroy() {
        resetAll()
        super.onDestroy()

    }

    private fun stopRecording() {
        try {
            mMediaRecorder.stop() // Stop recording and save file
            // It is a good practice to call this method when you're done using the MediaRecorder
            mMediaRecorder.release()
            Log.e("ScreenRecordingService", "STOP SUCCESS")

        } catch (e: Exception) {
            Log.e("ScreenRecordingService", "Error stopping recorder: ${e.message}")
        }

        mVirtualDisplay?.release() // Release virtual display
        mMediaProjection.stop() // Stop media projection
        mVirtualDisplay = null
    }

    private fun resetAll() {
        Log.d("RESTARTINGTESTING","SERVICE KILLED")
        stopForeground(STOP_FOREGROUND_DETACH)
        if (mVirtualDisplay != null) {
            mVirtualDisplay!!.release()
            mVirtualDisplay = null
        }

        mMediaRecorder.setOnErrorListener(null)
        mMediaRecorder.reset()
        mMediaRecorder.release()


        mMediaProjection.stop()



    }

}