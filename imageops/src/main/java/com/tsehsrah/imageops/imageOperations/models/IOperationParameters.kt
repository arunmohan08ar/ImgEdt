package com.tsehsrah.imageops.imageOperations.models

import android.graphics.Bitmap
import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes

interface IOperationParameters {
    val manager          : IOperationManager
    var primaryBmp       : Bitmap?
    var referenceBmp     : Bitmap?
    var secondaryBmp     : Bitmap?
    var rendered         : Bitmap?
    var tool             : IToolsStatus?
    var renderQuality    : Float
    var referenceMode    : ReferenceModes

    var refImgParams    : IImageParameters
    var secImgParams    : IImageParameters
    var noOfFreeCores   : Int

    fun setToolStatus(tool : IToolsStatus)
    fun clearRendered()
    fun clearAll()
    fun resetReferenceParameters()
    suspend fun finalize(quality:Float)
    suspend fun resetRendered(quality:Float)


}