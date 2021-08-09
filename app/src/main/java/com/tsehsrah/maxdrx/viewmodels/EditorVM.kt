package com.tsehsrah.maxdrx.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.lifecycle.*


import com.tsehsrah.imageops.imageOperations.configs.CONSTANTS.DEFAULT_TOOL_MODE
import com.tsehsrah.imageops.imageOperations.configs.ImageOperatorFactory
import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.models.*

import com.tsehsrah.imageops.imageOperations.operators.IImageOperator

import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.CONSTANTS.BLANK
import com.tsehsrah.maxdrx.configs.CONSTANTS.DEFAULT_PREVIEW_QUALITY
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREF_AUTO_SWITCH
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREF_PREV_QUALITY
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREF_SETTINGS
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREVIEW_MIN_QUALITY
import com.tsehsrah.maxdrx.configs.CONSTANTS.PREVIEW_QUALITY_REDUCTION_RATIO
import com.tsehsrah.maxdrx.configs.CONSTANTS.RENDERING_MAX_TIME
import com.tsehsrah.maxdrx.configs.CONSTANTS.RENDERING_MIN_TIME
import com.tsehsrah.maxdrx.configs.CONSTANTS.RENDERING_TIME_ANOMALY_COUNT_THRESHOLD
import com.tsehsrah.maxdrx.configs.EventAt
import com.tsehsrah.maxdrx.configs.FBEvent
import com.tsehsrah.maxdrx.configs.ImageLayers

import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.models.ILayerStates
import com.tsehsrah.maxdrx.repos.IImageRepository
import com.tsehsrah.maxdrx.utilities.IProcessMonitor

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.Exception
import kotlin.system.measureTimeMillis

@HiltViewModel
class EditorVM @Inject constructor(application: Application,
                                   private val sL:IServiceLocator,
                                   private val imgRepo:IImageRepository
    ):  AndroidViewModel(application) ,
        IOperationManager
{
    private val opParams           : IOperationParameters    by lazy{
                                                                sL.getNewOperationParameters(
                                                                    this as IOperationManager
                                                                )}
    private val proMonitor         : IProcessMonitor         by lazy { sL.getProcessMonitor() }
    private var operator           : IImageOperator
    private var previewQuality     : Float                   = DEFAULT_PREVIEW_QUALITY
    private var autoPreview        : Boolean                 = true
    private val layerState         : ILayerStates            by lazy { sL.getNewLayerState() }
    private var processCount       : AtomicInteger           = AtomicInteger(0)

    @Volatile
    private var isPreviewPending:Boolean=false


    private val _userHeadsUpString : MutableLiveData<String> = MutableLiveData(BLANK)
    val userHeadsUpString          : LiveData<String>        = _userHeadsUpString

    private val _activeLayer       : MutableLiveData
                                        <ImageLayers>        = MutableLiveData( ImageLayers.PREVIEW)
    val activeLayer                : LiveData<ImageLayers>   = _activeLayer

    private val _layerParameters   : MutableLiveData
                                         <IImageParameters>  by lazy{ MutableLiveData(
                                                                sL.getNewImageParameters()
                                                                )}
    val layerParameters            : LiveData
                                         <IImageParameters>  = _layerParameters

    private val _referenceMode     : MutableStateFlow
                                        <ReferenceModes>     = MutableStateFlow(ReferenceModes.PRIMARY)
    val referenceMode              : StateFlow
                                            <ReferenceModes> = _referenceMode

    private val _loadingStatus     : MutableStateFlow<Int>   = MutableStateFlow(processCount.get())
    val loadingStatus              : StateFlow<Int>          = _loadingStatus


    private val _selectionFragmentVisibility
                                   : MutableLiveData
                                          <Boolean>          = MutableLiveData(true)
    val selectionFragmentVisibility: LiveData<Boolean>       = _selectionFragmentVisibility

    private val _rendered                                    = MutableStateFlow(opParams.rendered)
    val rendered                   : StateFlow<Bitmap?>      = _rendered

    private val _toolsStatus       : MutableStateFlow
                                        <IToolsStatus>       by lazy{ MutableStateFlow(
                                                                sL.getNewToolStatus()
                                                                )}
    val toolsStatus                : StateFlow<IToolsStatus> = _toolsStatus

    private val _controlReset      : MutableLiveData<Unit>   = MutableLiveData(Unit)
    val controlReset               : LiveData<Unit>          = _controlReset

    private val _gestureStatus     : MutableLiveData
                                        <IGestureStatus>     by lazy {MutableLiveData(
                                                                sL.getNewGestureStatus()
                                                                )}
    val gestureStatus              : LiveData
                                        <IGestureStatus>     = _gestureStatus

    init {
        operator= ImageOperatorFactory.getOperator(DEFAULT_TOOL_MODE)
        updatePreferences()

        CoroutineScope(IO).launch {
            imgRepo.currentBmpSF.collect {
                it?.let {
                    incProcessCount()
                    opParams.primaryBmp = it
                    imgRepo.discardCurrentImage()
                    opParams.clearRendered()
                    try {
                        opParams.resetRendered(previewQuality)
                    }catch (oom:OutOfMemoryError){
                        sL.getAnalytics().logEvent(FBEvent.OOM,id="bmp",description = oom.toString())
                    }
                    operator.refresh()
                    _rendered.value = opParams.rendered
                    decProcessCount()
                }
                _controlReset.postValue(Unit)
            }
            System.gc()
        }
        CoroutineScope(IO).launch {
            toolsStatus.collect {
                updatePreview()
            }
        }

        CoroutineScope(IO).launch {
            imgRepo.secondaryImageSF.collect{bmp->
                bmp?.let {
                    opParams.secondaryBmp = it
                    imgRepo.discardSecondaryImage()
                    operator.refresh()
                    updatePreview()
                }
            }
        }
        CoroutineScope(IO).launch {
            imgRepo.referenceImageSF.collect{bmp->
                bmp?.let {
                    opParams.referenceBmp = it
                    imgRepo.discardReferenceImage()
                    _referenceMode.value=ReferenceModes.INDEPENDENT
                    operator.refresh()
                    updatePreview()
                }
            }
        }
        CoroutineScope(IO).launch {
            referenceMode.collect {
                opParams.referenceMode=it
                if(it==ReferenceModes.INDEPENDENT){
                    loadReferenceBmp()
                }else{
                    opParams.referenceBmp=null
                    updatePreview()
                }
                opParams.resetReferenceParameters()
            }
        }
    }

    fun toggleSelectionVisibility(){
        _selectionFragmentVisibility.postValue( selectionFragmentVisibility.value?.not())
    }

    fun setGestureStatus(status:IGestureStatus){
        _gestureStatus.postValue(status)
    }


    fun updateToolStatus(tool:IToolsStatus){
        if(toolsStatus.value.getMode()!=tool.getMode()){
            operator=sL.getImageOperatorFactory().getOperator(tool.getMode())
            operator.initOperator( opParams)
        }
        _toolsStatus.value=sL.getToolStatusCopy(tool)
    }

    fun applyChanges(){
        if(loadingStatus.value<1) {
            CoroutineScope(Dispatchers.Default).launch {
                incProcessCount()
                val time= measureTimeMillis {
                    opParams.noOfFreeCores=getCoreCount()
                    opParams.finalize(1f)
                    doProcess()
                    opParams.primaryBmp?.let {
                        try {
                            imgRepo.sveWorkImage(
                                sL.getAppContext(this@EditorVM),
                                it,
                                null
                            )
                        }catch (io:IOException){
                            sL.getAnalytics().logEvent(FBEvent.IOException)
                        }
                    }
                }
                _controlReset.postValue(Unit)
                _userHeadsUpString.postValue(
                    sL.getAppContext(this@EditorVM)
                         .getString(R.string.finished_in)
                            +"$time ms.")
                decProcessCount()
            }
        }
    }

    fun updateImageParameters(ip:IImageParameters?){
        if(ip==null){
            return
        }
        _layerParameters.postValue(ip)
        when(activeLayer.value){
            ImageLayers.REFERENCE-> opParams.refImgParams=ip
            ImageLayers.SECONDARY-> opParams.secImgParams=ip
            else->return
        }
        updatePreview()
    }
    fun setActiveLayer(next: ImageLayers){
        layerState.changeTo(layerParameters.value,next)
        _activeLayer.postValue(layerState.getCurrent())
        _layerParameters.postValue(layerState.getCurrentParameters())
    }
    fun setReferenceMode(mode:ReferenceModes){
        _referenceMode.value=mode
    }

    fun requestImageSave(format:Bitmap.CompressFormat){
        if(loadingStatus.value<1) {
            opParams.primaryBmp?.let {
                CoroutineScope(IO).launch {
                    incProcessCount()
                    opParams.primaryBmp?.let {
                        try{
                            imgRepo.saveImageToGallery(
                                it,
                                sL.getAppContext(this@EditorVM),
                                format
                            )
                        }catch (io:IOException){
                            sL.getAnalytics().logEvent(FBEvent.IOException)
                        }
                        _userHeadsUpString.postValue(
                            sL.getAppContext(this@EditorVM)
                                .getString(R.string.save_success)
                        )
                    }?:let{
                        _userHeadsUpString.postValue(sL.getAppContext(this@EditorVM)
                            .getString(R.string.something_wrong_on_file_save))
                    }
                    decProcessCount()
                }
            }
        }else{
            _userHeadsUpString.postValue(sL.getAppContext(this@EditorVM)
                .getString(R.string.please_wait_for)
                    +"${loadingStatus.value}"+
                    sL.getAppContext(this@EditorVM)
                        .getString(R.string.processes))
        }
    }

    fun clearAll(){
        opParams.clearAll()
        _rendered.value=null
    }
    fun updatePreferences(){
        val sp:SharedPreferences=sL.getAppContext(this)
            .getSharedPreferences(PREF_SETTINGS,Context.MODE_PRIVATE)

        previewQuality = (sp.getInt(PREF_PREV_QUALITY, (DEFAULT_PREVIEW_QUALITY*100).toInt())/100.0f)
        .coerceAtLeast(PREVIEW_MIN_QUALITY)
        autoPreview=sp.getBoolean(PREF_AUTO_SWITCH,true)

        opParams.renderQuality=previewQuality
    }
    private fun updatePreview(){
        CoroutineScope(Dispatchers.Default).launch {
            if(loadingStatus.value>0){
                isPreviewPending=true
            }else {
                incProcessCount()
                val time= measureTimeMillis {
                    opParams.noOfFreeCores=getCoreCount()
                    opParams.resetRendered(previewQuality)
                    doProcess()
                    _rendered.value = opParams.rendered
                }
                decProcessCount()
                val tAvg=proMonitor.updateTime(time.toInt())
                _userHeadsUpString.postValue(" ${(previewQuality*100).toInt() }% : $tAvg ms")
                evaluateRenderingTime(tAvg)

                if (isPreviewPending) {
                    isPreviewPending = false
                    updatePreview()
                }
            }
        }
    }

    private suspend fun doProcess(){
        incProcessCount()
        opParams.setToolStatus(toolsStatus.value)
        try {
            operator.perform(opParams)
        }catch (e:Exception){
            handleOOM("process")
        }
        decProcessCount()
    }

    private fun incProcessCount(){
        _loadingStatus.value= processCount.incrementAndGet()
    }
    private fun decProcessCount(){
        _loadingStatus.value= processCount.decrementAndGet()
    }
    private fun handleOOM(description:String){
        _userHeadsUpString.postValue(sL.getAppContext(this@EditorVM)
            .getString(R.string.something_wrong))
        if(autoPreview){
            decPreviewQuality()
        }
        sL.getAnalytics().logEvent(FBEvent.OOM,description = description,at= EventAt.EDITOR_VM)
    }
    private fun evaluateRenderingTime(t:Int){
        when(t) {
            in 0..RENDERING_MIN_TIME -> {
                proMonitor.count++
                if(proMonitor.count> RENDERING_TIME_ANOMALY_COUNT_THRESHOLD) {
                    if(autoPreview) {
                        incPreviewQuality()
                    }
                }
            }
            in RENDERING_MAX_TIME..Int.MAX_VALUE -> {
                proMonitor.count++
                if(proMonitor.count> RENDERING_TIME_ANOMALY_COUNT_THRESHOLD){
                    if(autoPreview) {
                        decPreviewQuality()
                    }
                }
            }
            else -> {
                proMonitor.count = 0
            }
        }
    }
    private fun decPreviewQuality(){
        previewQuality*= PREVIEW_QUALITY_REDUCTION_RATIO
        proMonitor.count = 0
    }
    private fun incPreviewQuality(){
        if(previewQuality< DEFAULT_PREVIEW_QUALITY) {
            previewQuality += (previewQuality * (1 - PREVIEW_QUALITY_REDUCTION_RATIO))
            if(previewQuality>1){
                previewQuality=1f
            }
            proMonitor.count = 0
        }
    }
    private fun getCoreCount():Int{
        return 2*Runtime.getRuntime().availableProcessors()
    }

    override fun loadSecondaryBmp() {
        try {
            imgRepo.setSecondary(sL.getAppContext(this@EditorVM),null)
        }catch (oom:OutOfMemoryError){
            handleOOM("sec")
        }
    }

    override fun loadReferenceBmp() {
        try{
            imgRepo.setReference(sL.getAppContext(this@EditorVM),null)
        }catch (oom:OutOfMemoryError){
            handleOOM("ref")
        }
    }

    override fun exceptionNotifier(e: Exception?, vme: VirtualMachineError?, s: String?) {
        if(vme!=null){
            handleOOM("from_process")
        }
    }

}