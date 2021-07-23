package com.tsehsrah.maxdrx.repos

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

import kotlinx.coroutines.flow.StateFlow

interface IImageRepository {
    val busyFlag            : StateFlow<Int>
    val currentBmpSF        : StateFlow<Bitmap?>
    val referenceImageSF    : StateFlow<Bitmap?>
    val secondaryImageSF    : StateFlow<Bitmap?>
    val workDirectoryUpdated: StateFlow<Int?>

    fun setCurrentPos(ctx: Context, pos:Int?, forceReload:Boolean)
    fun setReference(ctx: Context, at:Int?)
    fun setSecondary(ctx: Context, at:Int?)
    fun addNewWrkFiles(ctx: Context, at:Int, uri: Uri)
    suspend fun sveWorkImage(ctx: Context, bmp: Bitmap, pos:Int?)
    suspend fun saveImageToGallery(bmp: Bitmap, ctx: Context,format:Bitmap.CompressFormat)
    fun discardSecondaryImage()
    fun discardReferenceImage()
    fun discardCurrentImage()
    fun removeDataAt(ctx: Context, at:Int, total:Int, finishedCallBack:(b:Boolean)->Unit)
    fun clearAllWorkData(ctx: Context)
    suspend fun getBmp(ctx: Context, at: Int, isThump: Boolean): Bitmap?

}