package com.example.clicker.presentation.selfStreaming

import android.hardware.camera2.params.DynamicRangeProfiles
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Surface
import java.io.File
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.util.logging.Level
import java.util.logging.Logger

class EncoderWrapper(
    width: Int,
    height: Int,
    bitRate: Int,
    frameRate: Int,
    orientationHint: Int,
    outputFile: File,
    sendToTwitch:(ByteBuffer)->Unit


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

        EncoderThread(mEncoder!!, outputFile,sendToTwitch={buffer ->sendToTwitch(buffer)}, mOrientationHint)

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
        Log.d("stopStreamEncoding", "releasing encoder objects")


        val handler = mEncoderThread!!.getHandler()
        handler.sendMessage(handler.obtainMessage(EncoderThread.EncoderHandler.MSG_SHUTDOWN))
        Log.d("stopStreamEncoding", "sendMessage to handler")
        try {
            Log.d("stopStreamEncoding", "sendMessage to JOIN")
            mEncoderThread!!.join()//
        } catch (ie: InterruptedException ) {
            Log.w(TAG, "Encoder thread join() was interrupted", ie)
        }

        Log.d("stopStreamEncoding", "STOP AND RELEASE")
        mEncoder!!.stop()
        mEncoder!!.release()

        return true
    }

    //todo: I think this should run when start() is called
    /**
     * Notifies the encoder thread that a new frame is available to the encoder.
     * - This sends the message to the Handler that then tells the Thread to encode the frame
     */
    public fun frameAvailable() {
        val handler = mEncoderThread!!.getHandler()
        handler.sendMessage(handler.obtainMessage(
            EncoderThread.EncoderHandler.MSG_FRAME_AVAILABLE))
    }

    /* Wait for at least one frame to process so we don't have an empty video */
    public fun waitForFirstFrame() {

            mEncoderThread!!.waitForFirstFrame()
    }

    /**
     * Configures encoder
     */
    init {

        val codecProfile =1

        val format = MediaFormat.createVideoFormat(mMimeType, width, height)

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)


            format.setInteger(MediaFormat.KEY_PROFILE, codecProfile)
            format.setInteger(MediaFormat.KEY_COLOR_STANDARD, MediaFormat.COLOR_STANDARD_BT2020)
            format.setInteger(MediaFormat.KEY_COLOR_RANGE, MediaFormat.COLOR_RANGE_FULL)
            format.setInteger(MediaFormat.KEY_COLOR_TRANSFER, getTransferFunction())
            format.setFeatureEnabled(MediaCodecInfo.CodecCapabilities.FEATURE_HdrEditing, true)


        Log.d(TAG, "format: " + format)

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mEncoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }
    //todo: THIS SHOULD BE CALLED WHEN THE USER CLICKS THE RECORD
    public fun start() {

            mEncoder!!.start()

            // Start the encoder thread last.  That way we're sure it can see all of the state
            // we've initialized.
            mEncoderThread!!.start()
            mEncoderThread!!.waitUntilReady()

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


    /**
     * ---------------------------------------------- THE START OF EncoderThread -----------------------------------------------------------------
     * */
//    One way to create a Thread is to declare a class to be a subclass of Thread. This subclass should
//    override the run method of class Thread. An instance of the subclass can then be allocated and started.
    private class EncoderThread(mediaCodec: MediaCodec,
                                outputFile: File,
                                private val sendToTwitch:(ByteBuffer)->Unit,
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
         override fun run() {
            Log.d(TAG, "mReady run CALLED")
            Looper.prepare()

//            A Handler is the mechanism used to enqueue tasks on a Looper’s queue
            mHandler = EncoderHandler(this)    // must create on encoder thread
            Log.d(TAG, "encoder thread ready")
            synchronized (mLock) {
                Log.d(TAG, "mReady TRUE")
                mReady = true
                mLock.notify()    // signal waitUntilReady()
            }

            Looper.loop() // causes the thread to enter a tight loop in which it checks its MessageQueue for tasks

            synchronized (mLock) {
                Log.d(TAG, "mReady FALSE")
                mReady = false
                mHandler = null
            }
            Log.d(TAG, "looper quit")
        }

        /**
         * Waits until the encoder thread is ready to receive messages.
         * <p>
         * Call from non-encoder thread.
         */
        public fun waitUntilReady() {
            synchronized (mLock) {
                while (!mReady) {
                    Log.d(TAG, "mReady WAIT")
                    try {
                        mLock.wait()
                    } catch (ie: InterruptedException) { /* not expected */ }
                }
            }
        }
        /**
         * Waits until the encoder has processed a single frame.
         * <p>
         * Call from non-encoder thread.
         */
        public fun waitForFirstFrame() {
            synchronized (mLock) {
                while (mFrameNum < 1) {
                    try {
                        mLock.wait()
                    } catch (ie: InterruptedException) {
                        ie.printStackTrace();
                    }
                }
            }
            Log.d(TAG, "Waited for first frame");
        }

        /**
         * Drains the encoder output.
         * <p>
         * See notes for {@link EncoderWrapper#frameAvailable()}.
         */
        fun frameAvailable() {
           // Log.d("THREADframeAvailable", "frameAvailable")
            if (drainEncoder()) {
                synchronized (mLock) {
                    mFrameNum++
                    mLock.notify()
                }
            }
        }
        /**
         * Drains all pending output from the encoder, and adds it to the circular buffer.
         */
        public fun drainEncoder(): Boolean {
            val TIMEOUT_USEC: Long = 0     // no timeout -- check for buffers, bail if none
            var encodedFrame = false

            while (true) {

                
                var encoderStatus: Int = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC)
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    break;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Should happen before receiving buffers, and should only happen once.
                    // The MediaFormat contains the csd-0 and csd-1 keys, which we'll need
                    // for MediaMuxer.  It's unclear what else MediaMuxer might want, so
                    // rather than extract the codec-specific data and reconstruct a new
                    // MediaFormat later, we just grab it here and keep it around.
                    mEncodedFormat = mEncoder.getOutputFormat()
                    Log.d("drainEncoder", "encoder output format changed: " + mEncodedFormat)
                } else if (encoderStatus < 0) {
                    Log.w("drainEncoder", "unexpected result from encoder.dequeueOutputBuffer: " +
                            encoderStatus)
                    // let's ignore it
                } else {
                    //encodedData is the actual compressed video frames, encoded and ready for storage
                    var encodedData: ByteBuffer? = mEncoder.getOutputBuffer(encoderStatus)
                    if (encodedData == null) {
                        throw RuntimeException("encoderOutputBuffer " + encoderStatus +
                                " was null");
                    }

                    if ((mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // The codec config data was pulled out when we got the
                        // INFO_OUTPUT_FORMAT_CHANGED status.  The MediaMuxer won't accept
                        // a single big blob -- it wants separate csd-0/csd-1 chunks --
                        // so simply saving this off won't work.
                       Log.d("drainEncoder", "ignoring BUFFER_FLAG_CODEC_CONFIG")
                        mBufferInfo.size = 0
                    }

                    if (mBufferInfo.size != 0) {
                        // adjust the ByteBuffer values to match BufferInfo (not needed?)
                        //tells where the valid data starts and moves the buffer's read pointer to the start of the valid data.
                        encodedData.position(mBufferInfo.offset)
                        //prevents reading beyond the valid data.
                        //This ensures only the encoded frame data and not extra padding or old data is sent to
                        //the MediaMixer
                        encodedData.limit(mBufferInfo.offset + mBufferInfo.size)
                        //TODO: THIS IS WHERE i WOULD SEND THE ENCODED DATA

                        sendToTwitch(encodedData)


                        if (mVideoTrack == -1) {
                            //initialize the MediaMuxer if needed
                            mVideoTrack = mMuxer.addTrack(mEncodedFormat!!)
                            mMuxer.setOrientationHint(mOrientationHint)
                            mMuxer.start()
                            Log.d("drainEncoder", "Started media muxer")
                        }


                        //writes the encoded frame into the muxer.
                        mMuxer.writeSampleData(mVideoTrack, encodedData, mBufferInfo)
                        encodedFrame = true


                        Log.d("drainEncoder", "sent " + mBufferInfo.size + " bytes to muxer, ts=" +
                                    mBufferInfo.presentationTimeUs)

                    }

                    mEncoder.releaseOutputBuffer(encoderStatus, false)

                    if ((mBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.w("drainEncoder", "reached end of stream unexpectedly")
                        break      // out of while
                    }
                }
            }

            return encodedFrame
        }

        /**
         * Tells the Looper to quit.
         */
        fun shutdown() {
             Log.d(TAG, "shutdown the mMuxer")
            Looper.myLooper()!!.quit()
            mMuxer.stop()
            mMuxer.release()
        }



/**
 * --------------------------------------BEGIN THE EncoderHandler ----------------------------------------------------------
 * */
        /**
         * Handler for EncoderThread.  Used for messages sent from the UI thread (or whatever
         * is driving the encoder) to the encoder thread.
         * <p>
         * The object is created on the encoder thread.
         */
        public class EncoderHandler(et: EncoderThread): Handler() {
            companion object {
                val MSG_FRAME_AVAILABLE: Int = 0
                val MSG_SHUTDOWN: Int = 1
            }

            // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
            // but no real harm in it.
            private val mWeakEncoderThread = WeakReference<EncoderThread>(et)

            // runs on encoder thread
            public override fun handleMessage(msg: Message) {
                val what: Int = msg.what

                Log.v(TAG, "EncoderHandler: what=" + what)


                val encoderThread: EncoderThread? = mWeakEncoderThread.get()
                if (encoderThread == null) {
                    Log.w(TAG, "EncoderHandler.handleMessage: weak ref is null")
                    return
                }

                when (what) {
                    MSG_FRAME_AVAILABLE -> encoderThread.frameAvailable()
                    MSG_SHUTDOWN -> encoderThread.shutdown()
                    else -> throw RuntimeException("unknown message " + what)
                }
            }
        }
    }


}