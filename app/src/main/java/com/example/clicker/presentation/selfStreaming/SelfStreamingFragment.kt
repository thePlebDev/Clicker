package com.example.clicker.presentation.selfStreaming

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaCodec.CodecException
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Display
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.clicker.R
import com.example.clicker.databinding.FragmentSelfStreamingBinding
import com.example.clicker.nativeLibraryClasses.VideoEncoder
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.home.testing3DCode.VideoEncoderGLSurfaceViewComposable
import com.example.clicker.presentation.selfStreaming.surfaces.AutoFitSurfaceView
import com.example.clicker.presentation.selfStreaming.viewModels.SelfStreamingViewModel
import com.example.clicker.presentation.selfStreaming.views.SelfStreamingView
import com.example.clicker.ui.theme.AppTheme
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Math.max
import java.lang.Math.min
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


// , ConnectChecker -> this is causing the fragment to crash
class SelfStreamingFragment : Fragment() {

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var videoCapture: VideoCapture<Recorder>? = null
    private lateinit var recordingState:VideoRecordEvent
    private var currentRecording: Recording? = null
    private var audioEnabled = false
    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private val captureLiveStatus = MutableLiveData<String>()



    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    private var callback: MediaCodec.Callback? = null
    private var codec: MediaCodec? = null

//    val genericStream: GenericStream = GenericStream(requireActivity(),this)

    /**
     * the variable that acts as access to all the stream ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val selfStreamingViewModel: SelfStreamingViewModel by activityViewModels()
    /**
     * the variable that acts as access to all the logout ViewModel data. It is scoped with [activityViewModels](https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider)
     * */
    private val logoutViewModel: LogoutViewModel by activityViewModels()

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var camera: CameraDevice

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    /** Readers used as buffers for camera still shots */
    private lateinit var imageReader: ImageReader

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private lateinit var session: CameraCaptureSession



    private  var _binding: FragmentSelfStreamingBinding? = null

    /**
     * - The external version of [_binding]
     * */
    private val binding get() = _binding!!

    /** AndroidX navigation arguments */
//    private val args: FragmentSelfStreamingArgs by navArgs()

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }


    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(enumerateCameras(cameraManager))
    }

    private fun enumerateCameras(cameraManager: CameraManager):String {


        // Get list of all compatible cameras
        val cameraIds = cameraManager.cameraIdList.filter {
            val characteristics = cameraManager.getCameraCharacteristics(it)
            val capabilities = characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
            )
            capabilities?.contains(
                CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
            ) ?: false
        }.filter { id->
            id.toInt()==CameraCharacteristics.LENS_FACING_FRONT
        }

        cameraIds.forEach { id ->
            Log.d("cameraIdsCHECKS","cameraId -->$id")
        }
        return cameraIds[0]

    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
           val cameraId = enumerateCameras(cameraManager)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {





      // setUpCamera(requireActivity().applicationContext) // this is the normal one that works
        // Inflate the layout for this fragment
        _binding = FragmentSelfStreamingBinding.inflate(inflater, container, false)


        val view = binding.root
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
//                    VideoEncoderGLSurfaceViewComposable(
//                        context = requireContext(),
//                        modifier = Modifier.fillMaxSize()
//                    )
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
        binding.viewFinder.holder.addCallback(SurfaceHolderCallbackSetUp())

    }


    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating capture request
     * - Sets up the still image capture listeners
     */
    private fun initializeCamera(cameraId:String) = lifecycleScope.launch(Dispatchers.Main) {
        // Open the selected camera
       // this is what I need to figure out
        camera = openCamera(cameraManager, cameraId, cameraHandler)

        // Initialize an image reader which will be used to capture still photos
        val size = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            .getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!
        imageReader = ImageReader.newInstance(
            size.width, size.height, ImageFormat.JPEG, IMAGE_BUFFER_SIZE)

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(binding.viewFinder.holder.surface, imageReader.surface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)

        val captureRequest = camera.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW).apply { addTarget(binding.viewFinder.holder.surface) }

        // This will keep sending the capture request as frequently as possible until the
        // session is torn down or session.stopRepeating() is called
        session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)

        // Listen to the capture button

    }

    /**
     * Starts a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine
     */
    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Create a capture session using the predefined targets; this also involves defining the
        // session state callback to be notified of when the session is ready
        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }


    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device){}

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
                requireActivity().finish()
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    /**
     * setUpCamera gets a FUTURE and then runs the [bindPreview] once the future has computed
     * */


    //todo: this needs to be changed to work with the new camera2 API
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

    //todo: this needs to be changed to work with the new camera2 API
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

        Log.i("startRecordingAGAIN", "Recording started")
    }

    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { recordEvent ->
        // cache the recording state
        if (recordEvent !is VideoRecordEvent.Status)
            recordingState = recordEvent

        updateUI(recordEvent)

        if (recordEvent is VideoRecordEvent.Finalize) {
            // display the captured video
//            lifecycleScope.launch {
//                navController.navigate(
//                    CaptureFragmentDirections.actionCaptureToVideoViewer(
//                        event.outputResults.outputUri
//                    )
//                )
//            }
        }
        when(recordEvent) {
            is VideoRecordEvent.Start -> {
                Log.d("VideoRecordEventtESTING","START")

            }
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
        /** Maximum number of images that will be held in the reader's buffer */
        private const val IMAGE_BUFFER_SIZE: Int = 3
    }

    /** Helper class used to pre-compute shortest and longest sides of a [Size] */
    class SmartSize(width: Int, height: Int) {
        var size = Size(width, height)
        var long = max(size.width, size.height)
        var short = min(size.width, size.height)
        override fun toString() = "SmartSize(${long}x${short})"
    }


    /** Standard High Definition size for pictures and video */
    val SIZE_1080P: SmartSize = SmartSize(1920, 1080)

    /** Returns a [SmartSize] object for the given [Display] */
    fun getDisplaySmartSize(display: Display): SmartSize {
        val outPoint = Point()
        display.getRealSize(outPoint)
        return SmartSize(outPoint.x, outPoint.y)
    }

    /**
     * Returns the largest available PREVIEW size. For more information, see:
     * https://d.android.com/reference/android/hardware/camera2/CameraDevice and
     * https://developer.android.com/reference/android/hardware/camera2/params/StreamConfigurationMap
     */
    fun <T>getPreviewOutputSize(
        display: Display,
        characteristics: CameraCharacteristics,
        targetClass: Class<T>,
        format: Int? = null
    ): Size {

        // Find which is smaller: screen or 1080p
        val screenSize = getDisplaySmartSize(display)
        val hdScreen = screenSize.long >= SIZE_1080P.long || screenSize.short >= SIZE_1080P.short
        val maxSize = if (hdScreen) SIZE_1080P else screenSize

        // If image format is provided, use it to determine supported sizes; else use target class
        val config = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
        if (format == null)
            assert(StreamConfigurationMap.isOutputSupportedFor(targetClass))
        else
            assert(config.isOutputSupportedFor(format))
        val allSizes = if (format == null)
            config.getOutputSizes(targetClass) else config.getOutputSizes(format)

        // Get available sizes and sort them by area from largest to smallest
        val validSizes = allSizes
            .sortedWith(compareBy { it.height * it.width })
            .map { SmartSize(it.width, it.height) }.reversed()

        // Then, get the largest output size that is smaller or equal than our max size
        return validSizes.first { it.long <= maxSize.long && it.short <= maxSize.short }.size
    }

    inner class SurfaceHolderCallbackSetUp(): SurfaceHolder.Callback{
        override fun surfaceCreated(p0: SurfaceHolder) {
            Log.d("VIEWFINDERtESITNG","CREATED")
            val previewSize = getPreviewOutputSize(
                binding.viewFinder.display,
                characteristics,
                SurfaceHolder::class.java
            )
            Log.d("PREVIEWSIZE","height -->${previewSize.height}")
            Log.d("PREVIEWSIZE","width -->${previewSize.width}")

            binding.viewFinder.setAspectRatio(
                previewSize.width,
                previewSize.height
            )

            // To ensure that size is set, initialize camera in the view's thread
            view?.post { initializeCamera(enumerateCameras(cameraManager)) }
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            Log.d("VIEWFINDERtESITNG","SURFACE CHANGED")
        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
            Log.d("VIEWFINDERtESITNG","DESTROYED")
        }

    }


}



