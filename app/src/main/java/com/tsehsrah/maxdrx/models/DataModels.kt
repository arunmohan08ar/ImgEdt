package com.tsehsrah.maxdrx.models

import android.graphics.Bitmap
import com.tsehsrah.imageops.imageOperations.models.IImageParameters
import com.tsehsrah.maxdrx.configs.ImageLayers
import com.tsehsrah.maxdrx.di.IServiceLocator

class ItemImageSelectList(override var thumpBmp: Bitmap? = null,
                          override var nme: String = "Error"
) : IItemImageSelectList



class LayerState constructor(sL: IServiceLocator):ILayerStates {
    override lateinit var prvwParameters          : IImageParameters
    override lateinit var refParameters           : IImageParameters
    override lateinit var secParameters           : IImageParameters

    init {
        prvwParameters          = sL.getNewImageParameters()
        refParameters           = sL.getNewImageParameters()
        secParameters           = sL.getNewImageParameters()
    }

    private var activeLayer     : ImageLayers = ImageLayers.PREVIEW

    override fun getCurrent(): ImageLayers =activeLayer

    override fun changeTo(currentParameters: IImageParameters?, next: ImageLayers){
        if(currentParameters==null){
            return
        }
        when(activeLayer){
            ImageLayers.PREVIEW->{
                prvwParameters=currentParameters
            }
            ImageLayers.REFERENCE->{
            refParameters=currentParameters
            }
            ImageLayers.SECONDARY->{
            secParameters=currentParameters
            }
        }
        activeLayer=next
    }

    override fun getCurrentParameters(): IImageParameters {
        return when(activeLayer){
            ImageLayers.REFERENCE->{
                refParameters
            }
            ImageLayers.SECONDARY->{
                secParameters
            }else->{
                prvwParameters
            }
        }
    }
}



class RefVal : IRefVal {
    private var r: Int=128
    private var g: Int=128
    private var b: Int=128
    override fun gtR(): Int {
        return r
    }

    override fun gtG(): Int {
        return g
    }

    override fun gtB(): Int {
        return b
    }

    override fun stR(r:Int) {
        this.r=r
    }

    override fun stG(g:Int) {
        this.g=g
    }

    override fun stB(b:Int) {
        this.b=b
    }

    override fun stAll(r: Int, g: Int, b: Int) {
        this.r=r
        this.g=g
        this.b=b
    }
}




