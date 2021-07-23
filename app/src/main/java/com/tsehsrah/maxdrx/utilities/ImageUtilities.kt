package com.tsehsrah.maxdrx.utilities

import android.content.res.Resources
import android.graphics.Bitmap
import com.tsehsrah.maxdrx.configs.CONSTANTS.MAX_SCALE
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object ImageUtilities : IImageUtilities{
    val dspMin = Math.min(Resources.getSystem().displayMetrics.widthPixels,
                    Resources.getSystem().displayMetrics.heightPixels)

    fun getPrvRto(w:Int,h:Int,scle:Float):Float{
        val lrgr=((if(w<h)h else w).toFloat()
                /if(scle>1) scle else 1.toFloat())
        return Math.min((dspMin.toFloat() / lrgr), 1.toFloat())
    }
    override fun getImgRto(w:Int, h:Int, toVal:Int, scle:Float):Float{
        val lrgr=((if(w<h)h else w).toFloat()
                /if(scle>1) scle else 1.toFloat())
        return Math.min((toVal.toFloat() / lrgr), 1.toFloat())
    }


    override fun calculateXYtoAngle(x:Int, y:Int, ang:Double, scale:Float):Pair<Int,Int>{
        val xv:Int  = (x/ scale.coerceAtMost(MAX_SCALE)).toInt()
        val yv:Int  = (y/scale.coerceAtMost(MAX_SCALE)).toInt()

        val sine    = sin(Math.toRadians(ang)).toFloat()
        val cosine  = cos(Math.toRadians(ang)).toFloat()
        return Pair(((xv*cosine)+(yv*sine)).toInt(), ((yv*cosine)-(xv*sine)).toInt())
    }
    override fun angleBetweenLines(
        fX: Float,
        fY: Float,
        sX: Float,
        sY: Float,
        nfX: Float,
        nfY: Float,
        nsX: Float,
        nsY: Float
    ): Float {
        val angle1 = atan2((fY - sY).toDouble(), (fX - sX).toDouble()).toFloat()
        val angle2 =
            atan2((nfY - nsY).toDouble(), (nfX - nsX).toDouble()).toFloat()
        var angle = Math.toDegrees((angle1 - angle2).toDouble()).toFloat() % 360
        if (angle < -180f) angle += 360.0f
        if (angle > 180f) angle -= 360.0f
        return -angle/2
    }


    suspend fun getScaledBmp(bmp: Bitmap, w:Int, h:Int): Bitmap {
        return Bitmap.createScaledBitmap(bmp, w, h, false)
    }
    suspend fun getCroppeddBmp(bmp: Bitmap, w:Int, h:Int, x:Int, y:Int, wp:Int, hp:Int): Bitmap {
        var xv=x
        var yv=y
        var wv=w
        var hv=h
        if(xv<0){
            wv-=xv
            xv=0
        }
        if(yv<0){
            hv-=yv
            yv=0
        }
        if((xv+wv)>wp){
            wv-=(wp-xv-wv)
        }
        if((yv+hv)>hp){
            hv-=(hp-yv-hv)
        }
        if(hv<0) hv=1
        if(wv<0) wv=0

        return Bitmap.createBitmap(bmp,xv,yv,wv,hv)
    }



}