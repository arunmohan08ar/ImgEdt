package com.tsehsrah.maxdrx.fakes

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.tsehsrah.maxdrx.repos.IImageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeImageRepo: IImageRepository {
    private val bmp=Bitmap.createBitmap(20,20,Bitmap.Config.ARGB_8888)
    private val TAG="FakeRepo"
    private val _busyFlag   : MutableStateFlow<Int> = MutableStateFlow(0)
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

    override fun setCurrentPos(ctx: Context, pos: Int?, forceReload: Boolean) {
        _currentBmpSF.value= Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888)
    }

    override fun setReference(ctx: Context, at: Int?) {
        println("$TAG called set Reference")
    }

    override fun setSecondary(ctx: Context, at: Int?) {
        println("$TAG setSecondary was called")
    }

    override fun addNewWrkFiles(ctx: Context, at: Int, uri: Uri) {
        if(at==0){
            _currentBmpSF.value = bmp
            _workDirectoryUpdated.value=0
        }
    }

    override suspend fun sveWorkImage(ctx: Context, bmp: Bitmap, pos: Int?) {
        println("$TAG called sve work image")
        delay(10)
        setCurrentPos(ctx,pos,true)
        sveWorkImageWasCalled()
    }
    fun sveWorkImageWasCalled(){
    }

    override suspend fun saveImageToGallery(
        bmp: Bitmap,
        ctx: Context,
        format: Bitmap.CompressFormat
    ) {
        println("$TAG called sve image to gallery")
        sveImageToGalleryWasCalled()
    }
    fun sveImageToGalleryWasCalled(){
    }

    override fun discardSecondaryImage() {
        TODO("Not yet implemented")
    }

    override fun discardReferenceImage() {
        TODO("Not yet implemented")
    }

    override fun discardCurrentImage() {
        println("$TAG called discard current")
    }

    override fun removeDataAt(
        ctx: Context,
        at: Int,
        total: Int,
        finishedCallBack: (b: Boolean) -> Unit
    ) {
        removeDataAtWasCalled()
        println("$TAG remove data at called")
        finishedCallBack(true)
    }
    fun removeDataAtWasCalled(){
        println("$TAG remove data at called")

    }

    override fun clearAllWorkData(ctx: Context) {
        TODO("Not yet implemented")
    }

    override suspend fun getBmp(ctx: Context, at: Int, isThump: Boolean): Bitmap? =bmp
}