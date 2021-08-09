package com.tsehsrah.maxdrx.repos

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

object FakeImageRepo:IImageRepository {
    override val busyFlag: StateFlow<Int>
        get() = TODO("Not yet implemented")
    override val currentBmpSF: StateFlow<Bitmap?>
        get() = TODO("Not yet implemented")
    override val referenceImageSF: StateFlow<Bitmap?>
        get() = TODO("Not yet implemented")
    override val secondaryImageSF: StateFlow<Bitmap?>
        get() = TODO("Not yet implemented")
    override val workDirectoryUpdated: StateFlow<Int?>
        get() = TODO("Not yet implemented")

    override fun setCurrentPos(ctx: Context, pos: Int?, forceReload: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setReference(ctx: Context, at: Int?) {
        TODO("Not yet implemented")
    }

    override fun setSecondary(ctx: Context, at: Int?) {
        TODO("Not yet implemented")
    }

    override fun addNewWrkFiles(ctx: Context, at: Int, uri: Uri) {
        TODO("Not yet implemented")
    }

    override suspend fun sveWorkImage(ctx: Context, bmp: Bitmap, pos: Int?) {
        TODO("Not yet implemented")
    }

    override suspend fun saveImageToGallery(
        bmp: Bitmap,
        ctx: Context,
        format: Bitmap.CompressFormat
    ) {
        TODO("Not yet implemented")
    }

    override fun discardSecondaryImage() {
        TODO("Not yet implemented")
    }

    override fun discardReferenceImage() {
        TODO("Not yet implemented")
    }

    override fun discardCurrentImage() {
        TODO("Not yet implemented")
    }

    override fun removeDataAt(
        ctx: Context,
        at: Int,
        total: Int,
        finishedCallBack: (b: Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun clearAllWorkData(ctx: Context) {
        TODO("Not yet implemented")
    }

    override suspend fun getBmp(ctx: Context, at: Int, isThump: Boolean): Bitmap? {
        TODO("Not yet implemented")
    }
}