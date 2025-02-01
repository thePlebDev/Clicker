package com.example.clicker.presentation.selfStreaming

import android.hardware.camera2.params.DynamicRangeProfiles
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import java.io.File

class EncoderWrapper(
    width: Int,
    height: Int,
    bitRate: Int,
    frameRate: Int,
    orientationHint: Int,
    outputFile: File,

    ){

    private val mOrientationHint = orientationHint

    companion object {
        val TAG = "EncoderWrapper"

        val IFRAME_INTERVAL = 1 // sync one frame every second
        public const val VIDEO_CODEC_ID_HEVC: Int = 0
        public const val VIDEO_CODEC_ID_H264: Int = 1
        public const val VIDEO_CODEC_ID_AV1: Int = 2
    }
    /**
     * Returns the encoder's input surface.
     */
    public fun getInputSurface(): Surface {
        return mInputSurface
    }
    private val mInputSurface: Surface by lazy {
        mEncoder!!.createInputSurface()
    }

    private val mEncoderThread: EncoderThread? by lazy {

        EncoderThread(mEncoder!!, outputFile, mOrientationHint)

    }

    private val mVideoCodec = VIDEO_CODEC_ID_AV1

    private val mMimeType = idToType(mVideoCodec)
    private val dynamicRange = DynamicRangeProfiles.STANDARD

    private val mEncoder: MediaCodec? by lazy {
        MediaCodec.createEncoderByType("video/avc")
    }

    /**
     * Shuts down the encoder thread, and releases encoder resources.
     * <p>
     * Does not return until the encoder thread has stopped.
     */
    public fun shutdown(): Boolean {
        Log.d(TAG, "releasing encoder objects")


        val handler = mEncoderThread!!.getHandler()
        handler.sendMessage(handler.obtainMessage(EncoderThread.EncoderHandler.MSG_SHUTDOWN))
        try {
            mEncoderThread!!.join()
        } catch (ie: InterruptedException ) {
            Log.w(TAG, "Encoder thread join() was interrupted", ie)
        }

        mEncoder!!.stop()
        mEncoder!!.release()

        return true
    }
    //todo: I think this should run when start() is called
    /**
     * Notifies the encoder thread that a new frame is available to the encoder.
     */
    public fun frameAvailable() {
        val handler = mEncoderThread!!.getHandler()
        handler.sendMessage(handler.obtainMessage(
            EncoderThread.EncoderHandler.MSG_FRAME_AVAILABLE))
    }

    /**
     * Configures encoder
     */
    init {

        val codecProfile = when (mVideoCodec) {
            VIDEO_CODEC_ID_HEVC -> when {
                dynamicRange == DynamicRangeProfiles.HLG10 ->
                    MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10
                dynamicRange == DynamicRangeProfiles.HDR10 ->
                    MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10
                dynamicRange == DynamicRangeProfiles.HDR10_PLUS ->
                    MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10Plus
                else -> -1
            }
            VIDEO_CODEC_ID_AV1 -> when {
                dynamicRange == DynamicRangeProfiles.HLG10 ->
                    MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10
                dynamicRange == DynamicRangeProfiles.HDR10 ->
                    MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10HDR10
                dynamicRange == DynamicRangeProfiles.HDR10_PLUS ->
                    MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10HDR10Plus
                else -> -1
            }
            else -> -1
        }

        val format = MediaFormat.createVideoFormat(mMimeType, width, height)

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)

        if (codecProfile != -1) {
            format.setInteger(MediaFormat.KEY_PROFILE, codecProfile)
            format.setInteger(MediaFormat.KEY_COLOR_STANDARD, MediaFormat.COLOR_STANDARD_BT2020)
            format.setInteger(MediaFormat.KEY_COLOR_RANGE, MediaFormat.COLOR_RANGE_FULL)
            format.setInteger(MediaFormat.KEY_COLOR_TRANSFER, getTransferFunction())
            format.setFeatureEnabled(MediaCodecInfo.CodecCapabilities.FEATURE_HdrEditing, true)
        }

        Log.d(TAG, "format: " + format)

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }




    private fun idToType(videoCodecId: Int): String = when (videoCodecId) {
        VIDEO_CODEC_ID_H264 -> MediaFormat.MIMETYPE_VIDEO_AVC
        VIDEO_CODEC_ID_HEVC -> MediaFormat.MIMETYPE_VIDEO_HEVC
        VIDEO_CODEC_ID_AV1 -> MediaFormat.MIMETYPE_VIDEO_AV1
        else -> throw RuntimeException("Unexpected video codec id " + videoCodecId)
    }

    private fun getTransferFunction() = when (dynamicRange) {
        DynamicRangeProfiles.HLG10 -> MediaFormat.COLOR_TRANSFER_HLG
        DynamicRangeProfiles.HDR10 -> MediaFormat.COLOR_TRANSFER_ST2084
        DynamicRangeProfiles.HDR10_PLUS -> MediaFormat.COLOR_TRANSFER_ST2084
        else -> MediaFormat.COLOR_TRANSFER_SDR_VIDEO
    }


    private class EncoderThread(mediaCodec: MediaCodec,
                                outputFile: File,
                                orientationHint: Int): Thread() {
        val mEncoder = mediaCodec
        var mEncodedFormat: MediaFormat? = null
        val mBufferInfo = MediaCodec.BufferInfo()
        val mMuxer = MediaMuxer(outputFile.getPath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val mOrientationHint = orientationHint
        var mVideoTrack: Int = -1

        var mHandler: EncoderHandler? = null
        var mFrameNum: Int = 0


        //    In Java, every Object can be used as a lock. A synchronized method is a synchronized block whose lock is the instance of
//    the class instance. When a thread enters a synchronized block, it acquires the lock. And when a thread leaves the block,
//    it releases the lock.
        val mLock: Object = Object()

        @Volatile
        var mReady: Boolean = false // reads and writes to this field are atomic and writes are always made visible to other thread


        /**
         * Returns the Handler used to send messages to the encoder thread.
         */
        public fun getHandler(): EncoderHandler {
            synchronized(mLock) {
                // Confirm ready state.
                if (!mReady) {
                    throw RuntimeException("not ready")
                }
            }
            return mHandler!!
        }

        /**
         * Thread entry point.
         * <p>
         * Prepares the Looper, Handler, and signals anybody watching that we're ready to go.
         */
        public override fun run() {
            Looper.prepare()
            mHandler = EncoderHandler(this)    // must create on encoder thread
            Log.d(TAG, "encoder thread ready")
            synchronized (mLock) {
                mReady = true
                mLock.notify()    // signal waitUntilReady()
            }

            Looper.loop()

            synchronized (mLock) {
                mReady = false
                mHandler = null
            }
            Log.d(TAG, "looper quit")
        }


        /**
         * Handler for EncoderThread.  Used for messages sent from the UI thread (or whatever
         * is driving the encoder) to the encoder thread.
         * <p>
         * The object is created on the encoder thread.
         */
        public class EncoderHandler(et: EncoderThread) : Handler() {
            companion object {
                val MSG_FRAME_AVAILABLE: Int = 0
                val MSG_SHUTDOWN: Int = 1
            }

        }
    }

}