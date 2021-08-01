package com.tsehsrah.maxdrx.views

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.tsehsrah.imageops.imageOperations.models.IGestureStatus
import com.tsehsrah.imageops.imageOperations.models.IImageParameters
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.CONSTANTS.INVALID
import com.tsehsrah.maxdrx.configs.CONSTANTS.QUAD_TAP_DELAY
import com.tsehsrah.maxdrx.configs.ImageLayers
import com.tsehsrah.maxdrx.databinding.FragmentImagePreviewBinding
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.utilities.IImageUtilities

import com.tsehsrah.maxdrx.viewmodels.EditorVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin


@AndroidEntryPoint
class ImagePreviewFragment  : Fragment(R.layout.fragment_image_preview) {
    private lateinit var binding: FragmentImagePreviewBinding
    private val editorVM        : EditorVM by activityViewModels()
    @Inject
    lateinit var sL:IServiceLocator

    private lateinit var imgUtils:IImageUtilities
    private lateinit var gestureStatus   : IGestureStatus
    private lateinit var parameters      : IImageParameters

    private var oldFactor       : Float             = 1f
    private var fX              : Float             = 0f
    private var fY              : Float             = 0f
    private var sX              : Float             = 0f
    private var sY              : Float             = 0f
    private var previewAngle    : Float             = 0f
    private var ptrID1          : Int               = INVALID
    private var ptrID2          : Int               = INVALID
    private var imgv            : ImageView?        = null
    private var updatePending   : Boolean           = false

    private var quadTapFlags    : Boolean           = false

    private fun lateInits(){
        imgUtils=sL.getImageUtilities()
        gestureStatus= sL.getNewGestureStatus()
        parameters= sL.getNewImageParameters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentImagePreviewBinding.bind(view)
        lateInits()
        registerListeners()
    }

    private fun updatePreview(p:IImageParameters) {
        if (!updatePending) {
            updatePending   = true
            imgv?.scrollX   = p.scrollX
            imgv?.scrollY   = p.scrollY
            imgv?.scaleX    = p.scale
            imgv?.scaleY    = p.scale
            imgv?.rotation  = p.angle
            imgv?.adjustViewBounds
            updatePending   = false
        }
    }

    private fun registerListeners(){
        imgv=binding.imgvwPrvw
        lifecycleScope.launchWhenStarted{
            editorVM.rendered.collect {
                withContext(Dispatchers.Main){
                    it?.let{bmp->
                        imgv?.setImageBitmap(bmp)
                    }?:imgv?.setImageResource(R.drawable.ic_baseline_image_24)
                }
            }
        }
        lifecycleScope.launchWhenStarted{
            editorVM.loadingStatus.collect {
                withContext(Dispatchers.Main){
                    binding.procPrgrsBr.visibility=if(it >0) View.VISIBLE else View.GONE
                }
            }
        }

        editorVM.layerParameters.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    parameters=it
                    if(editorVM.activeLayer.value== ImageLayers.PREVIEW){
                        updatePreview(it)
                        previewAngle=it.angle
                    }
                }
            }
        )
        val headsUp= binding.userHeadsUp
        editorVM.userHeadsUpString.observe(viewLifecycleOwner,
            {
                headsUp.text=it
            }
        )

        editorVM.gestureStatus.observe(viewLifecycleOwner,
            {
                it?.let {
                    gestureStatus=it
                }
            }
        )
        val scaleGestureDetector = ScaleGestureDetector(context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    parameters.scale=(parameters.scale/oldFactor)*detector.scaleFactor
                    oldFactor=detector.scaleFactor
                    return super.onScale(detector)
                }
                override fun onScaleEnd(detector: ScaleGestureDetector?) {
                    parameters.scale=(parameters.scale/oldFactor)*(detector?.scaleFactor?:1.toFloat())
                    oldFactor=1f
                    super.onScaleEnd(detector)
                }
            }
        )
        val gestureDetector =GestureDetector(context,
            object : GestureDetector.SimpleOnGestureListener(){
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    if(quadTapFlags) {
                        if(gestureStatus.rotation){
                            parameters.resetAngle()
                        }
                        if (gestureStatus.pan){
                            parameters.resetPan()
                        }
                        if(gestureStatus.scale){
                            parameters.resetScale()
                        }
                    }else{
                        quadTapFlags=true
                        CoroutineScope(IO).launch {
                            delay(QUAD_TAP_DELAY)
                            quadTapFlags=false
                        }
                    }
                    return super.onDoubleTap(e)
                }
                override fun onScroll(
                    e1          : MotionEvent?,
                    e2          : MotionEvent?,
                    distanceX   : Float,
                    distanceY   : Float
                ): Boolean {
                    if(gestureStatus.pan) {
                        val values = imgUtils.calculateDiffXYtoAngle(
                            distanceX.toInt(),
                            distanceY.toInt(),
                            previewAngle.toDouble(),
                            parameters.scale
                        )
                        parameters.scrollX += values.first
                        parameters.scrollY += values.second
                    }
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }
            }
        )

        binding.root.setOnTouchListener {view, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
            if(gestureStatus.rotation){
                calculateRotation(motionEvent)
            }
            if(gestureStatus.scale){
                scaleGestureDetector.onTouchEvent(motionEvent)
            }
            editorVM.updateImageParameters(parameters)
            true
        }
    }
    private fun calculateRotation(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> ptrID1 = event.getPointerId(event.actionIndex)
            MotionEvent.ACTION_POINTER_DOWN -> {
                ptrID2 = event.getPointerId(event.actionIndex)
                sX = event.getX(event.findPointerIndex(ptrID1))
                sY = event.getY(event.findPointerIndex(ptrID1))
                fX = event.getX(event.findPointerIndex(ptrID2))
                fY = event.getY(event.findPointerIndex(ptrID2))
            }
            MotionEvent.ACTION_MOVE -> if (ptrID1 != MotionEvent.INVALID_POINTER_ID && ptrID2 != MotionEvent.INVALID_POINTER_ID) {
                val nsX: Float = event.getX(event.findPointerIndex(ptrID1))
                val nsY: Float = event.getY(event.findPointerIndex(ptrID1))
                val nfX: Float = event.getX(event.findPointerIndex(ptrID2))
                val nfY: Float = event.getY(event.findPointerIndex(ptrID2))
                val dAng=(.05f*imgUtils.angleBetweenLines(2,fX, fY, sX, sY, nfX, nfY, nsX, nsY))

                val sin1    = sin(Math.toRadians(parameters.angle.toDouble()))
                val cos1    = cos(Math.toRadians(parameters.angle.toDouble()))

                parameters.angle += dAng
                parameters.angle    %=360
                if(parameters.angle<0){
                    parameters.angle= 360-parameters.angle
                }

                val sin2    =sin(Math.toRadians(parameters.angle.toDouble()))
                val cos2    =cos(Math.toRadians(parameters.angle.toDouble()))

                val values=imgUtils.calculateDiffXYtoAngle(
                    ((sin2-sin1) * (parameters.scrollY+fY-sY)).toInt(),
                    ((cos2-cos1) * (parameters.scrollX+fX-sX)).toInt(),
                    parameters.angle.toDouble(),
                    parameters.scale
                )

                parameters.scrollX  +=values.first
                parameters.scrollY  +=values.second

            }
            MotionEvent.ACTION_UP           -> ptrID1 = INVALID
            MotionEvent.ACTION_POINTER_UP   -> ptrID2 = INVALID
            MotionEvent.ACTION_CANCEL       -> {
                ptrID1 = INVALID
                ptrID2 = INVALID
            }
        }
        return true
    }
}
