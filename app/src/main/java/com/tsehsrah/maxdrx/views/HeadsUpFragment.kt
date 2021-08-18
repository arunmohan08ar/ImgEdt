package com.tsehsrah.maxdrx.views

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope

import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.models.IGestureStatus
import com.tsehsrah.imageops.imageOperations.models.IImageParameters
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.CONSTANTS.INVALID
import com.tsehsrah.maxdrx.configs.CONSTANTS.LAYER_SELECTED_COLOUR
import com.tsehsrah.maxdrx.configs.CONSTANTS.TRANSPARENT
import com.tsehsrah.maxdrx.configs.ImageLayers
import com.tsehsrah.maxdrx.databinding.FragmentHeadsUpBinding
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.viewmodels.EditorVM
import com.tsehsrah.maxdrx.viewmodels.SelectorVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HeadsUpFragment  : Fragment(R.layout.fragment_heads_up) {
    private lateinit var binding: FragmentHeadsUpBinding
    private val selectorVM      : SelectorVM by activityViewModels()
    private val editorVM        : EditorVM by activityViewModels()
    @Inject
    lateinit var sL:IServiceLocator

    private lateinit var gestureStatus   : IGestureStatus
    private lateinit var layerParameters : IImageParameters
    private fun lateInits(){
        gestureStatus= sL.getNewGestureStatus()
        layerParameters= sL.getNewImageParameters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lateInits()
        binding= FragmentHeadsUpBinding.bind(view)
        registerListeners()
    }

    private fun registerListeners(){
        val preview         = binding.headsUpPreview
        val reference       = binding.headsUpReference
        val secondary       = binding.headsUpSecondary
        val pan             = binding.headsUpPan
        val scale           = binding.headsUpZoom
        val rotate          = binding.headsUpRotate
        val rightRotate     = binding.headsUpRightRotation
        val referenceText   = binding.headsUpReferenceText

        editorVM.layerParameters.observe(viewLifecycleOwner,{
            layerParameters=it
            val xy="${it.scrollX} , ${it.scrollY}"
            binding.headsUpXy.text=xy
            binding.headsUpScale.text=it.scale.toString()
            binding.headsUpAngle.text=it.angle.toString()
        })
        editorVM.activeLayer.observe(viewLifecycleOwner,{
            preview.setBackgroundColor(TRANSPARENT)
            reference.setBackgroundColor(TRANSPARENT)
            secondary.setBackgroundColor(TRANSPARENT)
            when(it){
                ImageLayers.REFERENCE -> reference.setBackgroundColor(LAYER_SELECTED_COLOUR)
                ImageLayers.SECONDARY -> secondary.setBackgroundColor(LAYER_SELECTED_COLOUR)
                else                  -> preview.setBackgroundColor(LAYER_SELECTED_COLOUR)
            }
        })

        lifecycleScope.launchWhenStarted{
            editorVM.referenceMode.collect {mode->
                withContext(Dispatchers.Main){
                    when(mode){
                        ReferenceModes.INDEPENDENT  -> referenceText.text=getText(R.string.ref_independent)
                        ReferenceModes.SECONDARY    -> referenceText.text=getText(R.string.ref_secondary)
                        else                        -> referenceText.text=getText(R.string.ref_primary)
                    }
                }
            }
        }

        selectorVM.headsUpPreview.observe(viewLifecycleOwner,{
            it?.let {
                preview.setImageBitmap(it)
            }
        })
        selectorVM.headsUpReference.observe(viewLifecycleOwner,{
            it?.let {
                reference.setImageBitmap(it)
            }?:let{
                reference.setImageResource(R.drawable.ic_baseline_image_24)
            }
        })
        selectorVM.headsUpSecondary.observe(viewLifecycleOwner,{
            it?.let {
                secondary.setImageBitmap(it)
            }
        })

        preview.setOnClickListener {
            editorVM.setActiveLayer(ImageLayers.PREVIEW)
        }
        reference.setOnClickListener {
            editorVM.setActiveLayer(ImageLayers.REFERENCE)
        }
        secondary.setOnClickListener {
            editorVM.setActiveLayer(ImageLayers.SECONDARY)
        }


        pan.setOnClickListener {showOptionsPopup(it,PopUp.ScrollBars)}
        scale.setOnClickListener {showOptionsPopup(it,PopUp.ScrollBars)}
        rotate.setOnClickListener {showOptionsPopup(it,PopUp.ScrollBars)}

        rightRotate.setOnClickListener {
            val angle=editorVM.layerParameters.value?.angle?:0.0f
            val nAngle= if(angle >=0f  &&   angle  <90f ) 90f
            else if (angle>=90f   && angle  <180f) 180f
            else if (angle>=180f  && angle  <270f) 270f
            else 0f
            editorVM.updateImageParameters(editorVM.layerParameters.value?.setAngle(nAngle))
        }

        reference.setOnLongClickListener {
            showOptionsPopup(it,PopUp.Reference)
            true
        }
        pan.setOnLongClickListener {
            gestureStatus.pan= !gestureStatus.pan
            pan.setImageResource(
                if(gestureStatus.pan) {
                    R.drawable.ic_baseline_pan
                }else{
                    R.drawable.ic_baseline_pan_off
                }
            )
            editorVM.setGestureStatus(gestureStatus)
            true
        }
        rotate.setOnLongClickListener {
            gestureStatus.rotation= !gestureStatus.rotation
            rotate.setImageResource(
                if(gestureStatus.rotation) {
                    R.drawable.ic_baseline_rotation
                }else{
                    R.drawable.ic_baseline_rotation_off
                }
            )
            editorVM.setGestureStatus(gestureStatus)
            true
        }
        scale.setOnLongClickListener {
            gestureStatus.scale= !gestureStatus.scale
            scale.setImageResource(
                if(gestureStatus.scale) {
                    R.drawable.ic_baseline_zoom
                }else{
                    R.drawable.ic_baseline_zoom_off_24
                }
            )
            editorVM.setGestureStatus(gestureStatus)
            true
        }
    }

    private fun showOptionsPopup(v:View,popUp:PopUp){
        val inflater        =  layoutInflater
        val popupView: View = inflater.inflate(when(popUp){
            PopUp.Reference ->R.layout.popup_reference_modes
            PopUp.ScrollBars->R.layout.popup_fine_sensitivity
                                                          },
            LinearLayout(context),
            false
        )
        val width       = LinearLayout.LayoutParams.MATCH_PARENT
        val height      = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        popupWindow.showAtLocation(v, Gravity.CENTER,0,0)
        when(popUp){
            PopUp.Reference->manageRefPopup(popupWindow)
            else -> manageScrollbarPopup(popupWindow,v)
        }


    }

    private fun manageRefPopup(popupWindow:PopupWindow){
        val popupView=popupWindow.contentView
        popupView.findViewById<TextView>(R.id.reference_mode_primary)?.setOnClickListener {
            editorVM.setReferenceMode(ReferenceModes.PRIMARY)
            selectorVM.setReferencePos(INVALID)
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.reference_mode_secondary)?.setOnClickListener {
            editorVM.setReferenceMode(ReferenceModes.SECONDARY)
            selectorVM.setReferencePos(INVALID)
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.reference_mode_independent)?.setOnClickListener {
            editorVM.setReferenceMode(ReferenceModes.INDEPENDENT)
            popupWindow.dismiss()
        }
    }

    private fun manageScrollbarPopup(w:PopupWindow,v:View){
        val wv:View=w.contentView
        val t1=wv.findViewById<TextView>(R.id.popup_fine_tv1)
        val t2=wv.findViewById<TextView>(R.id.popup_fine_tv2)
        val t3=wv.findViewById<TextView>(R.id.popup_fine_tv3)
        val s1=wv.findViewById<SeekBar>(R.id.popup_fine_sb1)
        val s2=wv.findViewById<SeekBar>(R.id.popup_fine_sb2)
        val s3=wv.findViewById<SeekBar>(R.id.popup_fine_sb3)
        t3.text=getText(R.string.sensitivity)
        when(v.tag){
            getString(R.string.pan_tag)->{
                t1.text=getText(R.string.x)
                t2.text=getText(R.string.y)
            }else->{
                t1.text=getText(R.string.fine)
                t2.text=getText(R.string.coarse)
            }
        }
        fun setSensitivity(i:Int){
            sensitivity=(i/100f)
        }
        fun resetSeekbar(sb:SeekBar){
            ignoreFlag=true
            sb.progress=50
            old=50f
        }

        fun update(sb:Int,mode:MoreMode){
            var value=0f
            fun fineVal(){
                value=((sb-old)/10f)*sensitivity
                old=sb.toFloat()
            }
            fun coarseVal(){
                value=(sb-old)*sensitivity
                old=sb.toFloat()
            }
            fun panVal(){
                value= (sb-old)*sensitivity*5
                if(value>1||value<1){
                    old=sb.toFloat()
                }
            }


            when(v.tag){
                getString(R.string.pan_tag)->{
                    when(mode){
                        MoreMode.First-> {
                            panVal()
                            layerParameters.scrollX-=value.toInt()
                        }
                        MoreMode.Second -> {
                            panVal()
                            layerParameters.scrollY+=value.toInt()
                        }
                    }
                }getString(R.string.rotate_tag)->{
                    when(mode){
                        MoreMode.First->fineVal()
                        MoreMode.Second -> coarseVal()
                    }
                    layerParameters.angle+=value
                }getString(R.string.zoom_tag)->{
                    when(mode){
                        MoreMode.First->fineVal()
                        MoreMode.Second -> coarseVal()
                    }
                    layerParameters.scale+=(value/100f)
                }
            }
            editorVM.updateImageParameters(layerParameters)
        }
        s1.progress = 50
        s2.progress = 50
        s3.progress = (100* sensitivity).toInt()

        s1.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                if(ignoreFlag){
                    ignoreFlag=false
                    return
                }
                update(p1,MoreMode.First)
            }
            override fun onStartTrackingTouch(p0: SeekBar) {}
            override fun onStopTrackingTouch(p0: SeekBar) {
                resetSeekbar(p0)
            }

        })
        s2.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {
                if(ignoreFlag){
                    ignoreFlag=false
                    return
                }
                update(p1,MoreMode.Second)
            }

            override fun onStartTrackingTouch(p0: SeekBar) {}

            override fun onStopTrackingTouch(p0: SeekBar) {
                resetSeekbar(p0)
            }

        })
        s3.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar) {}
            override fun onStopTrackingTouch(p0: SeekBar) {
                setSensitivity(p0.progress)
            }
        })
    }


    companion object {
        private var ignoreFlag:Boolean=false
        private var sensitivity:Float=.5f
        private var old:Float=50f
        enum class PopUp{
            Reference,
            ScrollBars
        }
        enum class MoreMode{
            First,Second
        }
    }
}