package com.tsehsrah.imageops.imageOperations.utilities

import android.graphics.Bitmap
import android.graphics.Matrix
import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.models.*


object BitmapUtils : IBitmapUtilities{

    override suspend fun getScaledBmp(bmp: Bitmap, ratio: Float): Bitmap {
        try {
            return Bitmap.createScaledBitmap(
                bmp,
                (bmp.width * ratio).toInt(),
                (bmp.height * ratio).toInt(),
                false
            )
        }catch (oom:OutOfMemoryError){
            throw oom
        }
    }

    override suspend fun validateRefCache(params: IOperationParameters) {
        Cache.ref=if(params.referenceMode==ReferenceModes.INDEPENDENT){
            params.referenceBmp?.let { ref ->
                Cache.rX = (params.refImgParams.scrollX
                        * params.refImgParams.scale
                        * params.renderQuality
                        ).toInt()
                Cache.rY = (params.refImgParams.scrollY
                        * params.refImgParams.scale
                        * params.renderQuality
                        ).toInt()
                getValidatedCache(
                    Cache.ref,
                    ref,
                    params.refImgParams,
                    params.renderQuality
                )
            }
        }else{
            Cache.ref=null
            Cache.resetRefOffsets()
            null
        }
    }

    override suspend fun validateSecCache(params: IOperationParameters) {
        params.secondaryBmp?.let { sec ->
            Cache.sec = getValidatedCache(
                Cache.sec,
                sec,
                params.secImgParams,
                params.renderQuality
            )
            Cache.sX = (params.secImgParams.scrollX
                    * params.secImgParams.scale
                    * params.renderQuality
                    ).toInt()
            Cache.sY = (params.secImgParams.scrollY
                    * params.secImgParams.scale
                    * params.renderQuality
                    ).toInt()
        }
    }

    private suspend fun getValidatedCache(
        cache   : IImageCache?,
        bmp     : Bitmap,
        p       : IImageParameters,
        quality : Float
    ): IImageCache {
        bmp.setHasAlpha(true)
        val ratio=quality*p.scale
        val ht=(bmp.height*ratio).toInt()
        val wt= (bmp.width*ratio).toInt()
        cache?.let { kh ->
            if (kh.isValid(quality, wt, ht, p.angle, p.scale)) {
                return kh
            } else {
                kh.clear()
            }
        }
        var bm: Bitmap = getScaledBmp(bmp, p.scale * quality)
        bm.setHasAlpha(true)
        bm = getRotatedBmp(bm, p.angle)
        return ImageCache(wt, ht, p.angle, p.scale, quality).setBmp(bm)
    }

    private fun getRotatedBmp(src: Bitmap, ang: Float): Bitmap {
        src.setHasAlpha(true)
        val matrix = Matrix()
        matrix.postRotate(ang)
        return Bitmap.createBitmap(
                src,
                0,
                0,
                src.width,
                src.height,
                matrix,
                true
        )
    }

}
