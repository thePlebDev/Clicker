package com.example.clicker.presentation.selfStreaming

import android.content.ContentValues
import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.clicker.R
import com.example.clicker.databinding.FragmentHomeBinding
import com.example.clicker.databinding.FragmentSelfStreamingBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.selfStreaming.viewModels.SelfStreamingViewModel
import com.example.clicker.presentation.selfStreaming.views.SelfStreamingView
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.rtmp.ConnectChecker
import com.example.clicker.rtmp.GenericStream
import com.example.clicker.ui.theme.AppTheme
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.Locale


// , ConnectChecker -> this is causing the fragment to crash
class SelfStreamingFragment : Fragment() {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var videoCapture: VideoCapture<Recorder>? = null
    private lateinit var recordingState:VideoRecordEvent
    private var currentRecording: Recording? = null
    private var audioEnabled = false
    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private val captureLiveStatus = MutableLiveData<String>()

//    val genericStream: GenericStream = GenericStream(requireActivity(),this)

    /**
     * the variable that acts as access to all the stream ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val selfStreamingViewModel: SelfStreamingViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the logout ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val logoutViewModel: LogoutViewModel by activityViewModels()



    private  var _binding: FragmentSelfStreamingBinding? = null

    /**
     * - The external version of [_binding]
     * */
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpCamera(requireActivity().applicationContext)
        // Inflate the layout for this fragment
        _binding = FragmentSelfStreamingBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    SelfStreamingView(
                        selfStreamingViewModel = selfStreamingViewModel,
                        startStream = { startStreamButtonClick() },
                        stopStream = { stopStreamButtonClick() },
                        logoutOfTwitch = {
                            logoutViewModel.setLoggedOutStatus("TRUE")
                            findNavController().navigate(R.id.action_selfStreamingFragment_to_logoutFragment)

                        }

                    )
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    /**
     * setUpCamera gets a FUTURE and then runs the [bindPreview] once the future has computed
     * */
    private fun setUpCamera(context:Context){

        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(context))
    }
    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val codec = MediaCodec.createEncoderByType("video/avc") // H.264 codec
        val format = MediaFormat.createVideoFormat("video/avc", 200, 300).apply {
            setInteger(MediaFormat.KEY_BIT_RATE, 500000) // Adjust bitrate
            setInteger(MediaFormat.KEY_FRAME_RATE, 30)  // Adjust frame rate
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1) // Interval between I-frames
        }
        //moves the codec to the Configured stage
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        //createInputSurface() must be called after configure
        val inputSurface = codec.createInputSurface()

        //moves the codec to the Executing stage
        codec.start()

        //where the camera data is coming from?
        var preview : Preview = Preview.Builder()
            .build()
        //get a reference to the XML view
        val previewView =binding.previewView

        //build and return the camera object
        var cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        //set where the camera data is going to be shown
       // preview.setSurfaceProvider(previewView.getSurfaceProvider())
        preview.setSurfaceProvider { request ->
            request.provideSurface(inputSurface, ContextCompat.getMainExecutor(requireContext())) {
                println("CameraX preview frames are now routed to MediaCodec")
            }
        }
        // build a recorder, which can:
        //   - record video/audio to MediaStore(only shown here), File, ParcelFileDescriptor
        //   - be used create recording(s) (the recording performs recording)
        val recorder = Recorder.Builder()
            .build()
        videoCapture = VideoCapture.withOutput(recorder)


        cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)


    }

    private fun bindPreview2() {
        val codec = MediaCodec.createEncoderByType("video/avc") // H.264 codec
        val format = MediaFormat.createVideoFormat("video/avc", 200, 300).apply {
            setInteger(MediaFormat.KEY_BIT_RATE, 500000) // Adjust bitrate
            setInteger(MediaFormat.KEY_FRAME_RATE, 30)  // Adjust frame rate
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1) // Interval between I-frames
        }
        //moves the codec to the Configured stage
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        //createInputSurface() must be called after configure
        val inputSurface = codec.createInputSurface()

        //moves the codec to the Executing stage
        codec.start()

    }



    private fun startStreamButtonClick(){
        if (!this@SelfStreamingFragment::recordingState.isInitialized || recordingState is VideoRecordEvent.Finalize) {
            //THIS IS GOING TO BE TRIGGERED FIRST BECAUSE isInitialized IS FALSE

            startRecording()
        } else {
            when (recordingState) {
                is VideoRecordEvent.Start -> {
                    currentRecording?.pause()
                    //  captureViewBinding.stopButton.visibility = View.VISIBLE
                }
                is VideoRecordEvent.Pause -> currentRecording?.resume()
                is VideoRecordEvent.Resume -> currentRecording?.pause()
                else -> throw IllegalStateException("recordingState in unknown state")
            }
        }
    }
    private fun stopStreamButtonClick(){

        if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
            return
        }

        val recording = currentRecording
        if (recording != null) {
            recording.stop()
            currentRecording = null
        }
    }


    // Implements VideoCapture use case, including start and stop capturing.
    private fun initializeUI() {




    }


    /**
     * Kick start the video recording
     *   - config Recorder to capture to MediaStoreOutput
     *   - register RecordEvent Listener
     *   - apply audio request from user
     *   - start recording!
     * After this function, user could start/pause/resume/stop recording and application listens
     * to VideoRecordEvent for the current recording status.
     */

    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val name = "CameraX-recording-" +
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        // configure Recorder and Start recording to the mediaStoreOutput.
        currentRecording = videoCapture?.output
            ?.prepareRecording(requireActivity(), mediaStoreOutput)
//            .apply { if (audioEnabled) withAudioEnabled() }
            ?.start(mainThreadExecutor, captureListener)?.apply{

                selfStreamingViewModel.setIsStreamLive(true)


            }

        Log.i(TAG, "Recording started")
    }

    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status)
            recordingState = event

        updateUI(event)

        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
//            lifecycleScope.launch {
//                navController.navigate(
//                    CaptureFragmentDirections.actionCaptureToVideoViewer(
//                        event.outputResults.outputUri
//                    )
//                )
//            }
        }
    }
    private fun updateUI(event: VideoRecordEvent) {
//        val state = if (event is VideoRecordEvent.Status) recordingState.getNameString()
//        else event.getNameString()
        when (event) {
            is VideoRecordEvent.Status -> {
                //The status report of the recording in progress
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
                Log.d("updateUI", "Status")
            }

            is VideoRecordEvent.Start -> {
//                showUI(UiState.RECORDING, event.getNameString())
                Log.d("updateUI", "Start")
            }

            is VideoRecordEvent.Finalize -> {
                //Indicates the finalization of recording
//                showUI(UiState.FINALIZED, event.getNameString())
                Log.d("updateUI", "Finalize")

                selfStreamingViewModel.setIsStreamLive(false)


            }

            is VideoRecordEvent.Pause -> {
//                captureViewBinding.captureButton.setImageResource(R.drawable.ic_resume)
                Log.d("updateUI", "Finalize")
            }

            is VideoRecordEvent.Resume -> {
//                captureViewBinding.captureButton.setImageResource(R.drawable.ic_pause)
                Log.d("updateUI","Resume")
            }
        }
    }


    companion object {
        // default Quality selection if no input from UI
        const val DEFAULT_QUALITY_IDX = 0
        val TAG:String = SelfStreamingFragment::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

//    override fun onConnectionStarted(url: String) {
//        binding.liveButton.text = "START RTMP"
//    }
//
//    override fun onConnectionSuccess() {
//        //update for success
//        binding.liveButton.text = "SUCCESS RTMP"
//    }
//
//    override fun onConnectionFailed(reason: String) {
//        binding.liveButton.text = "FAILED RTMP"
//    }
//
//    override fun onDisconnect() {
//
//    }
//
//    override fun onAuthError() {
//
//    }
//
//    override fun onAuthSuccess() {
//        TODO("Not yet implemented")
//    }


}

