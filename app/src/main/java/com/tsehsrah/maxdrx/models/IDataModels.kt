package com.tsehsrah.maxdrx.models

import android.graphics.Bitmap
import com.tsehsrah.imageops.imageOperations.models.IImageParameters
import com.tsehsrah.maxdrx.configs.ImageLayers


interface IItemImageSelectList {
    var thumpBmp: Bitmap?
    var nme:String
}


interface IRefVal {
    fun gtR():Int
    fun gtG():Int
    fun gtB():Int
    fun stR(r:Int)
    fun stG(g:Int)
    fun stB(b:Int)
    fun stAll(r:Int,g:Int,b:Int)
}

interface ILayerStates{
    var prvwParameters          : IImageParameters
    var refParameters           : IImageParameters
    var secParameters           : IImageParameters

    fun getCurrent(): ImageLayers
    fun changeTo(currentParameters: IImageParameters?, next: ImageLayers)
    fun getCurrentParameters(): IImageParameters

}
