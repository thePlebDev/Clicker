package com.example.clicker.presentation.selfStreaming

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.media.MediaCodec
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.Display
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.clicker.R
import com.example.clicker.databinding.FragmentSelfStreamingBinding
import com.example.clicker.presentation.authentication.logout.LogoutViewModel
import com.example.clicker.presentation.selfStreaming.surfaces.AutoFitSurfaceView
import com.example.clicker.presentation.selfStreaming.viewModels.SelfStreamingViewModel
import com.example.clicker.presentation.selfStreaming.views.SelfStreamingView
import com.example.clicker.ui.theme.AppTheme
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.lang.Math.max
import java.lang.Math.min
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


// , ConnectChecker -> this is causing the fragment to crash
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    private val RECORDER_VIDEO_BITRATE: Int = 10_000_000

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
        cameraManager.getCameraCharacteristics(getFirstCameraIdFacing(cameraManager)?:"")
    }


    /** File where the recording will be saved */
    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }

    /** [EncoderWrapper] utility class */
    private val encoder: EncoderWrapper = createEncoder()

    /** Orientation of the camera as 0, 90, 180, or 270 degrees */
    private val orientation: Int by lazy {
        characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
    }


    private fun createEncoder(): EncoderWrapper {

        val size = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!
            .getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!

        val cameraConfig = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!
        val secondsPerFrame =
            cameraConfig.getOutputMinFrameDuration(SurfaceHolder::class.java, size) /
                    1_000_000_000.0
        // Compute the frames per second to let user select a configuration
        val fps = if (secondsPerFrame > 0) (1.0 / secondsPerFrame).toInt() else 0


        return EncoderWrapper(
            outputFile = outputFile,
            orientationHint = orientation,
            width = size.width,
            height = size.height,
            bitRate = RECORDER_VIDEO_BITRATE,
            frameRate = fps,

            )
    }

    /** Creates a [File] named with the current date and time */
    private fun createFile(context: Context, extension: String): File {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
        return File(context.filesDir, "VID_${sdf.format(Date())}.$extension")
    }




        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
                        startStream = {
                            //todo: THIS NEEDS TO CALL TO START THE ENCODING
                            encoder.start()
                            selfStreamingViewModel.setIsStreamLive(true)
                                      },
                        stopStream = {
                            //todo: THIS NEEDS TO CALL TO END THE ENCODING
//                            cvRecordingStarted.block() this is just a variable

                            /* Wait for at least one frame to process so we don't have an empty video */
//                            encoder.waitForFirstFrame() don't need because I don't use a MediaRecorder

                            session.stopRepeating()
                            session.close()

//                            pipeline.clearFrameListener() don't add yet
                            encoder.shutdown()
                            selfStreamingViewModel.setIsStreamLive(false)

                                     },
                        logoutOfTwitch = {
                            logoutViewModel.setLoggedOutStatus("TRUE")
                            findNavController().navigate(R.id.action_selfStreamingFragment_to_logoutFragment)
                        },
                        switchCamera = {
//                           val id = getNextCameraId(
//                                cameraManager,
//                                currCameraId = selfStreamingViewModel.cameraId.value
//                            )
                            selfStreamingViewModel.setCameraId()
//
                            session.stopRepeating()
                            session.close()
                            camera.close()


                            Handler().postDelayed({
                                // doSomethingHere()
                                Log.d("switchCameraCall","viewModelId -->${selfStreamingViewModel.cameraId.value}")
                                initializeCamera(selfStreamingViewModel.cameraId.value?:"")
                            }, 2000)


                        }

                    )
                }
            }
        }
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        /**
         * THE NEW CODE i NEED
         *
         * **/
        val capabilities = characteristics.get(
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!
        val cameraConfig = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
        val secondsPerFrame =
            cameraConfig.getOutputMinFrameDuration( SurfaceHolder::class.java, size) /
                    1_000_000_000.0
        // Compute the frames per second to let user select a configuration
        val fps = if (secondsPerFrame > 0) (1.0 / secondsPerFrame).toInt() else 0
        val fpsLabel = if (fps > 0) "$fps" else "N/A"

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(binding.viewFinder.holder.surface, imageReader.surface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)
        encoder.configure()


        /**
         * NEW VERSION BELOW
         * */
        // You will use the preview capture template for the combined streams
        // because it is optimized for low latency; for high-quality images, use
        // TEMPLATE_STILL_CAPTURE, and for a steady frame rate use TEMPLATE_RECORD
        val requestTemplate = CameraDevice.TEMPLATE_RECORD
        val combinedRequest = session.device.createCaptureRequest(requestTemplate)
        // Link the Surface targets with the combined request
        combinedRequest.addTarget(binding.viewFinder.holder.surface)
        //combinedRequest.addTarget(encoder.getInputSurface()) //

        // Sets user requested FPS for all targets
        combinedRequest.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(fps, fps))
        combinedRequest.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
            CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_PREVIEW_STABILIZATION)
        /**
         * NEW VERSION ABOVE
         * - it is the same as the one below, just more explicit
         * */

        //BELOW IS THE ONE THAT WORKS
//        val captureRequest = camera.createCaptureRequest(
//            CameraDevice.TEMPLATE_PREVIEW).apply { addTarget(binding.viewFinder.holder.surface) }

        // This will keep sending the capture request as frequently as possible until the
        // session is torn down or session.stopRepeating() is called
        session.setRepeatingRequest(combinedRequest.build(), null, cameraHandler)


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

    fun getFirstCameraIdFacing(cameraManager: CameraManager,
                               facing: Int = CameraMetadata.LENS_FACING_BACK): String? {
        try {
            // Get list of all compatible cameras
            val cameraIds = cameraManager.cameraIdList.filter {
                val characteristics = cameraManager.getCameraCharacteristics(it)
                val capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                capabilities?.contains(
                    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) ?: false
            }

            // Iterate over the list of cameras and return the first one matching desired
            // lens-facing configuration
            cameraIds.forEach {
                val characteristics = cameraManager.getCameraCharacteristics(it)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == facing) {
                    return it
                }
            }
            selfStreamingViewModel.setCameraId(
                cameraIds.firstOrNull()
            )

            // If no camera matched desired orientation, return the first one from the list
            return cameraIds.firstOrNull()
        } catch (e: CameraAccessException) {
            e.message?.let { Log.e(TAG, it) }
            return null
        }

    }
    private fun filterCompatibleCameras(cameraIds: Array<String>,
                                        cameraManager: CameraManager): List<String> {
        return cameraIds.filter {
            val characteristics = cameraManager.getCameraCharacteristics(it)
            characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.contains(
                CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) ?: false
        }
    }

    private fun filterCameraIdsFacing(cameraIds: List<String>, cameraManager: CameraManager,
                                      facing: Int): List<String> {
        return cameraIds.filter {
            val characteristics = cameraManager.getCameraCharacteristics(it)
            characteristics.get(CameraCharacteristics.LENS_FACING) == facing
        }
    }

    fun getNextCameraId(cameraManager: CameraManager, currCameraId: String? = null): String? {
        // Get all front, back and external cameras in 3 separate lists
        val cameraIds = filterCompatibleCameras(cameraManager.cameraIdList, cameraManager)
        val backCameras = filterCameraIdsFacing(
            cameraIds, cameraManager, CameraMetadata.LENS_FACING_BACK)
        val frontCameras = filterCameraIdsFacing(
            cameraIds, cameraManager, CameraMetadata.LENS_FACING_FRONT)
        val externalCameras = filterCameraIdsFacing(
            cameraIds, cameraManager, CameraMetadata.LENS_FACING_EXTERNAL)

        // The recommended order of iteration is: all external, first back, first front
        val allCameras = (externalCameras + listOf(
            backCameras.firstOrNull(), frontCameras.firstOrNull())).filterNotNull()

        // Get the index of the currently selected camera in the list
        val cameraIndex = allCameras.indexOf(currCameraId)

        // The selected camera may not be in the list, for example it could be an
        // external camera that has been removed by the user
        return if (cameraIndex == -1) {
            Log.d("CameraIDChecking","1")

            // Return the first camera from the list
            allCameras.getOrNull(0)

        } else {
            // Return the next camera from the list, wrap around if necessary

            Log.d("CameraIDChecking","0")
            allCameras.getOrNull((cameraIndex + 1) % allCameras.size)
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
            val id = getFirstCameraIdFacing(
                cameraManager
            )?:""

            // To ensure that size is set, initialize camera in the view's thread
            view?.post { initializeCamera(id) }


        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            Log.d("VIEWFINDERtESITNG","SURFACE CHANGED")
        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
            Log.d("VIEWFINDERtESITNG","DESTROYED")
        }

    }


}





