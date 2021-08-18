package com.tsehsrah.maxdrx.repos

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

import com.tsehsrah.maxdrx.configs.CONSTANTS
import com.tsehsrah.maxdrx.configs.CONSTANTS.IMAGE_SELECT_THUMP_SIZE
import com.tsehsrah.maxdrx.configs.CONSTANTS.INVALID
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.utilities.IImageFileUtilities

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicInteger

class ImageRepo constructor(private val sL:IServiceLocator) : IImageRepository  {

    private val fileUtils   : IImageFileUtilities   = sL.getImageFileUtilities()

    private var exposedPos  : Int                   = 0
    private var referencePos: Int?                  = null
    private var currentPos  : Int                   = INVALID
    private var processCount: AtomicInteger         = AtomicInteger(0)

    private val _busyFlag   : MutableStateFlow<Int> = MutableStateFlow(processCount.get())
    override val busyFlag   : StateFlow<Int>        = _busyFlag

    private val _currentBmpSF                       = MutableStateFlow<Bitmap?>(null)
    override val currentBmpSF
                            : StateFlow<Bitmap?>    = _currentBmpSF

    private val _referenceImageSF                   = MutableStateFlow<Bitmap?>(null)
    override val referenceImageSF
                            : StateFlow<Bitmap?>    = _referenceImageSF

    private val _secondaryImageSF                   = MutableStateFlow<Bitmap?>(null)
    override val secondaryImageSF
                            : StateFlow<Bitmap?>    = _secondaryImageSF

    private val _workDirectoryUpdated               = MutableStateFlow<Int?>(null)
    override val workDirectoryUpdated
                            : StateFlow<Int?>       = _workDirectoryUpdated


    override fun setCurrentPos(ctx:Context, pos:Int?, forceReload:Boolean){
        var at= currentPos
        if(pos!=null){
            at=pos
        }
        if(at == currentPos && !forceReload) {
            return
        }
        currentPos=at
        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()) {
            _currentBmpSF.value = getBmp(ctx, at, false)
        }
    }

    override fun setReference(ctx: Context, at:Int?){
        if(at!=null) {
            referencePos = at
        }
        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()) {
            _referenceImageSF.value = getBmp(ctx, referencePos?: currentPos, false)
        }
    }

    override fun setSecondary(ctx: Context, at:Int?){
        if(at!=null) {
            exposedPos = at
        }
        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()) {
            _secondaryImageSF.value = getBmp(ctx, exposedPos, false)
        }
    }


    override fun addNewWrkFiles(ctx: Context, at:Int, uri: Uri){
        if(workDirectoryUpdated.value==at){
            _workDirectoryUpdated.value=null
        }
        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()) {
            incRunningProcessCount()
            val bmp = fileUtils.getImgFromURI  (uri, ctx)
            bmp?.let{
                sveWorkImage(ctx,it,at)
                _workDirectoryUpdated.value=at
            }
            decRunningProcessCount()
        }
    }

    override suspend fun sveWorkImage(ctx: Context, bmp:Bitmap, pos:Int?){
        var at=currentPos
        if(pos!=null){
            at= pos
        }
        coroutineScope {
            listOf(
                async {
                    incRunningProcessCount()
                    fileUtils.saveWorkBmp(
                        bmp,
                        ctx,
                        CONSTANTS.IMG_TMP_DIR,
                        CONSTANTS.TEMP_IMG_NAME+at
                    )
                    decRunningProcessCount()
                  },
                async {
                    incRunningProcessCount()
                    val rto=sL.getImageUtilities()
                        .getImageRatio(bmp.width,bmp.height,IMAGE_SELECT_THUMP_SIZE,1f)
                    fileUtils.saveWorkBmp(
                        Bitmap.createScaledBitmap(
                            bmp,
                            (bmp.width * rto).toInt(),
                            (bmp.height * rto).toInt(),
                            false
                        ),
                        ctx,
                        CONSTANTS.IMG_TMP_DIR, CONSTANTS.THUMP_IMG_NAME + at
                    )
                    decRunningProcessCount()
                }
            ).awaitAll()
            if(at== currentPos) {
                setCurrentPos(ctx,at,true)
            }
        }
    }

    override suspend fun saveImageToGallery(bmp:Bitmap, ctx: Context,format:Bitmap.CompressFormat){
        incRunningProcessCount()
        fileUtils.saveImageToGallery(bmp, ctx, CONSTANTS.IMG_OP_DIR,format)
        decRunningProcessCount()
    }

    override fun discardSecondaryImage(){
        _secondaryImageSF.value=null
        System.gc()
    }
    override fun discardReferenceImage(){
        _referenceImageSF.value=null
        System.gc()
    }
    override fun discardCurrentImage(){
        _currentBmpSF.value=null
        System.gc()
    }

    override fun removeDataAt(ctx: Context, at:Int, total:Int, finishedCallBack:(b:Boolean)->Unit){
        incRunningProcessCount()
        CoroutineScope(sL.getIODispatcher()).launch(sL.getIODispatcher()){
            if(fileUtils.removeWorkBmp(ctx, CONSTANTS.IMG_TMP_DIR, CONSTANTS.TEMP_IMG_NAME+at)){
                fileUtils.removeWorkBmp(ctx, CONSTANTS.IMG_TMP_DIR, CONSTANTS.THUMP_IMG_NAME+at)
                if(at!=total)
                for (i in at+1 .. total){
                    if(!fileUtils.renameWorkBmp(
                        ctx,
                        CONSTANTS.IMG_TMP_DIR,
                        CONSTANTS.TEMP_IMG_NAME+i,
                        CONSTANTS.TEMP_IMG_NAME+(i-1)
                        )
                    ){
                        finishedCallBack(false)
                        decRunningProcessCount()
                        return@launch
                    }
                    fileUtils.renameWorkBmp(
                        ctx,
                        CONSTANTS.IMG_TMP_DIR,
                        CONSTANTS.TEMP_IMG_NAME+i,
                        CONSTANTS.THUMP_IMG_NAME+(i-1)
                    )
                }
            }
            finishedCallBack(true)
            decRunningProcessCount()
        }

    }

    override fun clearAllWorkData(ctx:Context){
        Thread{
            fileUtils.clearWorkDirectory(ctx, CONSTANTS.IMG_TMP_DIR)
        }.start()
        _currentBmpSF.value         = null
        _referenceImageSF.value     = null
        _secondaryImageSF.value     = null
        _workDirectoryUpdated.value = null
    }

    override suspend fun getBmp(ctx: Context, at: Int, isThump: Boolean): Bitmap? {
        incRunningProcessCount()
        val bmp=fileUtils.getWorkBmp(
            ctx,
            CONSTANTS.IMG_TMP_DIR,
            (if (isThump) CONSTANTS.THUMP_IMG_NAME else CONSTANTS.TEMP_IMG_NAME) + at
        )
        decRunningProcessCount()
        return  bmp
    }

    private fun incRunningProcessCount(){
        _busyFlag.value= processCount.incrementAndGet()
    }
    private fun decRunningProcessCount(){
        _busyFlag.value= processCount.decrementAndGet()
    }

}