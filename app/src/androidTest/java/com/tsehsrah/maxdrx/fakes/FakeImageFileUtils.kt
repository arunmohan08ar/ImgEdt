package com.tsehsrah.maxdrx.fakes

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.tsehsrah.maxdrx.utilities.IImageFileUtilities

class FakeImageFileUtils: IImageFileUtilities {
    private val TAG="FakeImageUtils"
    val bmp=Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888)

    override fun getImgFromURI(uri: Uri?, context: Context): Bitmap? {
        println("$TAG get image from uri called")
        return bmp
    }

    override fun saveWorkBmp(bitmap: Bitmap, context: Context, path: String, fileName: String) {
        println("$TAG save work bmp called")
    }

    override fun getWorkBmp(context: Context, path: String, fileName: String): Bitmap? {
        println("$TAG get work bmp was called")
        return bmp
    }

    override fun removeWorkBmp(context: Context, path: String, fileName: String): Boolean {
        println("$TAG remove work bmp called")
        return true
    }

    override fun renameWorkBmp(
        context: Context,
        path: String,
        fileName: String,
        filename2: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun saveImageToGallery(
        bitmap: Bitmap,
        context: Context,
        folderName: String,
        format: Bitmap.CompressFormat
    ) {
        println("$TAG save image to gallery was called")
    }

    override fun clearWorkDirectory(ctx: Context, path: String) {
        TODO("Not yet implemented")
    }
}