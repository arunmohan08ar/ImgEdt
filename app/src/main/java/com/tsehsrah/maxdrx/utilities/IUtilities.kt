package com.tsehsrah.maxdrx.utilities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.tsehsrah.maxdrx.di.IServiceLocator

interface IImageSelectUtility{
    fun requestImage(ctx:Context,resultLauncher: ActivityResultLauncher<Intent>,sL:IServiceLocator)
}

interface IImageUtilities {
    fun getImageRatio(
        w: Int,
        h: Int,
        toVal: Int,
        scale: Float
    ): Float
    fun calculateDiffXYtoAngle(
        x:Int,
        y:Int,
        ang:Double,
        scale:Float
    ):Pair<Int,Int>

    fun angleBetweenLines(
        ratio:Int,
        fX: Float,
        fY: Float,
        sX: Float,
        sY: Float,
        nfX: Float,
        nfY: Float,
        nsX: Float,
        nsY: Float
    ): Float

}

interface IFilePrechecks{
    fun checkFilePermisson(context: Context, req_code: Int): Boolean
}
interface IImageFileUtilities{
    fun getImgFromURI(uri: Uri?, context: Context): Bitmap?
    fun saveWorkBmp(bitmap: Bitmap, context: Context,path:String, fileName: String)
    fun getWorkBmp(context: Context,path: String,fileName: String):Bitmap?
    fun removeWorkBmp(context: Context,path: String,fileName: String):Boolean
    fun renameWorkBmp(context: Context,path: String,fileName: String,filename2:String):Boolean
    fun saveImageToGallery(bitmap: Bitmap, context: Context, folderName: String,format:Bitmap.CompressFormat)
    fun clearWorkDirectory(ctx:Context,path:String)
}

interface IProcessMonitor{
    var count:Int
    fun updateTime(v:Int):Int
    fun reset()
}
