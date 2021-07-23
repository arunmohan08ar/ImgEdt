package com.tsehsrah.maxdrx.utilities

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.tsehsrah.maxdrx.configs.CONSTANTS.IMG_SVE_PATH_REL

import java.io.*

object FileUtilsL : IImageFileUtilities {

    override fun getImgFromURI(uri: Uri?, context: Context): Bitmap? {
         try {
            if (uri == null) {
                return null
            }
            val imageStream: InputStream? = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(imageStream)
        } catch (fe: FileNotFoundException) {
            return null
        }
    }

    override fun saveWorkBmp(bitmap: Bitmap, context: Context, path:String, fileName: String) {
            val directory = File(context.filesDir,  path)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, fileName)
            if(file.exists()){
                file.delete()
            }

        saveImageToStream(bitmap, FileOutputStream(file),Bitmap.CompressFormat.PNG)
        }
    override fun getWorkBmp(context: Context, path: String, fileName: String):Bitmap?{
        val fle = File(context.filesDir, path+fileName)
        val uri:Uri=Uri.fromFile(fle)

        return try {
            val imageStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(imageStream)
        } catch (fe: FileNotFoundException) {
            null
        }catch (oom:OutOfMemoryError){
            null
        }
    }
    override fun removeWorkBmp(context: Context, path: String, fileName: String):Boolean{
        val fle = File(context.filesDir, path+fileName)
        if(fle.exists()){
            return fle.delete()
        }
        return false
    }
    override fun renameWorkBmp(context: Context, path: String, fileName: String, filename2:String):Boolean{
        val fle = File(context.filesDir, path+fileName)
        val fle2 = File(context.filesDir, path+filename2)
        if(fle.exists()){
            return fle.renameTo(fle2)
        }
        return false
    }


    override fun saveImageToGallery(bitmap: Bitmap, context: Context, folderName: String, format:Bitmap.CompressFormat) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH,IMG_SVE_PATH_REL + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri),Bitmap.CompressFormat.PNG)
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + "/" + folderName)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val ext=when(format){
                Bitmap.CompressFormat.JPEG->".jpeg"
                Bitmap.CompressFormat.WEBP->".webp"
                else->"png"
            }
            val fileName = System.currentTimeMillis().toString() + ext
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file),format)
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
       // values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?,format:Bitmap.CompressFormat) {
        if (outputStream != null) {
            try {
                bitmap.compress(format, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun clearWorkDirectory(ctx:Context, path:String){
        val directory = File(ctx.filesDir,  path)
        if (directory.exists()) {
            directory.deleteRecursively()
        }
    }

}
/*


suspend fun sveBmp(bmp : Bitmap,appCTX:Context,addToGallery:Boolean,asTmp:Boolean) {
    coroutineScope {
        withContext(Dispatchers.IO) {
            Log.i("sveeee","req rcvd in sveutl")

            val envfl = appCTX.filesDir
            if (envfl != null) {
                if (!envfl.exists()) {
                    envfl.mkdir()
                }
            }
            var fle: File? = File(envfl, "/maxDR/")
            fle?.mkdirs()
            val cmprs_frmt = Bitmap.CompressFormat.PNG
            val frmt_str = ".png"
            fle = File(envfl, "/maxDR/" + "Test" + "testt" + frmt_str)
            Log.i("SVRRR", "fle 2=$fle")
            try {
                FileOutputStream(fle).use { out1 ->
                    bmp.compress(cmprs_frmt, 100, out1)
                    if (addToGallery) ad_2_glry(fle.toString(), appCTX)
                }
            } catch (e: IOException) {
                Log.i("SVRRR", e.toString())
            }
        }
    }
}

fun ad_2_glry(pth: String, ctx:Context) {
    Log.i("sveeee","req rcvd in ad2gal")
    val values = ContentValues()
    if (Build.VERSION.SDK_INT > 28) values.put(
        MediaStore.Images.Media.DATE_TAKEN,
        System.currentTimeMillis()
    )
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    values.put(MediaStore.MediaColumns.DATA, pth)
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, "TESTT")

    ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
}


*/
/*fun chckStrgAvlblt(appContext: Context,rqrmnt:Long){

    val storageManager = appContext.getSystemService<StorageManager>()!!
    val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
    val availableBytes: Long =
        storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
    if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
        storageManager.allocateBytes(
            appSpecificInternalDirUuid, rqrmnt)
    } else {
        val storageIntent = Intent().apply {

            action = ACTION_MANAGE_STORAGE
        }
    }

}*/
