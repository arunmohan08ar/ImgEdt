package com.tsehsrah.imageops.imageOperations.models

import android.graphics.Bitmap
import com.tsehsrah.imageops.imageOperations.configs.ImageOperators
import java.lang.Exception


interface IToolsStatus {
    fun setMode(m:ImageOperators)
    fun setSeekbar1 (i:Int)
    fun setSeekbar2 (i:Int)
    fun setReference(r:Int)
    fun setExposed  (e:Int)
    fun setIntensity(i:Int)

    fun getMode     ():ImageOperators
    fun getSeekbar1 ():Int
    fun getSeekbar2 ():Int
    fun getReference():Int
    fun getExposed  ():Int
    fun getIntensity():Int

    fun getFrom     ():Int
    fun getTo       ():Int
    fun getIntensityFF():Int

}


interface IOperatorDescription {
    val name            : String
    val description     : String
    val hasMoreOptions  : Boolean
}

interface IImageCache{
    val angle   : Float
    val w       : Int
    val h       : Int
    val scale   : Float
    val quality : Float
    fun getBmp():Bitmap?
    fun setBmp(bmp:Bitmap?):IImageCache
    fun clear()
    fun isValid(
        quality : Float  = 1f,
        w       : Int    = 1,
        h       : Int    = 1,
        angle   : Float  = 0f,
        scale   : Float  = 1f
    ):Boolean{
        return (this.getBmp()!=null
                &&this.angle.equals(angle)
                && this.w   == w
                && this.h   == h
                && this.scale== scale
                && this.quality== quality
                )
    }
}

interface IImageParameters {
    var scrollX : Int
    var scrollY : Int
    var scale   : Float
    var angle   : Float
    fun resetPan(){
        scrollX = 0
        scrollY = 0
    }
    fun resetScale(){
        scale   = 1f
    }
    fun resetAngle(){
        angle   = 0f
    }
    fun setCoordinates(x:Int,y:Int):IImageParameters{
        this.scrollX=x
        this.scrollY=y
        return this
    }
    fun setAngle(angle:Float):IImageParameters{
        this.angle=angle
        return this
    }
    fun setScale(scale:Float):IImageParameters{
        this.scale=scale
        return this
    }
}

interface ICache{
    var ref:IImageCache?
    var sec:IImageCache?
    var rX:Int
    var rY:Int
    var sX:Int
    var sY:Int

    fun clearAll()
    fun resetRefOffsets()
    fun resetSecOffsets()
}


interface IGestureStatus {
    var pan     : Boolean
    var scale   : Boolean
    var rotation: Boolean
}

interface IErrors{
    val e   : Exception?
    val vme : VirtualMachineError?
    val s   : String?
}
