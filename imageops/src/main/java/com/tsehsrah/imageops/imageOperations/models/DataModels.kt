package com.tsehsrah.imageops.imageOperations.models

import android.graphics.Bitmap
import com.tsehsrah.imageops.imageOperations.configs.CONSTANTS.DEFAULT_TOOL_MODE
import com.tsehsrah.imageops.imageOperations.configs.ImageOperators

class ImageCache (
    override val w      : Int   = 1,
    override val h      : Int   = 1,
    override val angle  : Float = 0f,
    override val scale  : Float = 1f,
    override val quality: Float = 1f
):IImageCache{
    private var _bmp:Bitmap?=null

    @Synchronized
    override fun getBmp(): Bitmap? {
        return _bmp
    }
    @Synchronized
    override fun setBmp(bmp:Bitmap?):IImageCache{
        _bmp=bmp
        return this
    }

    override fun clear() {
        _bmp=null
    }

}


class ToolsStatus constructor() : IToolsStatus {
    private var seekBar1            : Int       = 0
    private var seekBar2            : Int       = 100
    private var mode                : ImageOperators = DEFAULT_TOOL_MODE
    private var referencePosition   : Int       = -1
    private var exposedPosition     : Int       = 0
    private var intensity           : Int       = 0

    constructor(tool: IToolsStatus) : this() {
        this.mode               = tool.getMode()
        this.seekBar1           = tool.getSeekbar1()
        this.seekBar2           = tool.getSeekbar2()
        this.referencePosition  = tool.getReference()
        this.exposedPosition    = tool.getExposed()
        this.intensity          = tool.getIntensity()
    }
    override fun setMode(m: ImageOperators) {
        mode=m
    }
    override fun getMode(): ImageOperators {
        return mode
    }
    override fun setSeekbar1(i: Int) {
        seekBar1=i
    }
    override fun setSeekbar2(i: Int) {
        seekBar2=i
    }
    override fun setReference(r: Int) {
        referencePosition=r
    }
    override fun setExposed(e: Int) {
        exposedPosition=e
    }
    override fun setIntensity(i: Int) {
        intensity=i
    }

    override fun getSeekbar1    (): Int = seekBar1
    override fun getSeekbar2    (): Int = seekBar2
    override fun getReference   (): Int = referencePosition
    override fun getExposed     (): Int = exposedPosition
    override fun getIntensity   (): Int = intensity

    override fun getFrom        (): Int {
        return (seekBar1*255)/100
    }
    override fun getTo          (): Int {
        return (seekBar2*255)/100
    }

    override fun getIntensityFF(): Int {
        return (intensity*255)/100
    }
}


class OperatorDescription(override val name        : String,
                   override var description : String,
                   override val hasMoreOptions: Boolean
) : IOperatorDescription


class ImageParameters(
    override var scrollX: Int   = 0,
    override var scrollY: Int   = 0,
    override var scale  : Float = 1f,
    override var angle  : Float = 0f,
) : IImageParameters

class GestureStatus : IGestureStatus {
    override var pan     : Boolean = true
    override var scale   : Boolean = true
    override var rotation: Boolean =true
}

object Cache:ICache{
    override var ref: IImageCache? = null
    override var sec: IImageCache? = null
    override var rX: Int=0
    override var rY: Int=0
    override var sX: Int=0
    override var sY: Int=0

    override fun clearAll() {
        ref = null
        sec = null
    }

    override fun resetRefOffsets() {
        rX=0
        rY=0
    }
    override fun resetSecOffsets() {
        sX=0
        sY=0
    }


}

class OperationError(
    override val e  : Exception?,
    override val vme: VirtualMachineError?,
    override val s  : String?
) :IErrors