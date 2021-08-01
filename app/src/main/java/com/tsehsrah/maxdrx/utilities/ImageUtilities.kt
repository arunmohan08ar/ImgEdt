package com.tsehsrah.maxdrx.utilities

import com.tsehsrah.maxdrx.configs.CONSTANTS.MAX_SCALE
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object ImageUtilities : IImageUtilities{

    override fun getImageRatio(w:Int, h:Int, toVal:Int, scale:Float):Float{
        val lrgr=((if(w<h)h else w).toFloat()
                /if(scale>1) scale else 1.toFloat())
        return Math.min((toVal.toFloat() / lrgr), 1.toFloat())
    }


    override fun calculateDiffXYtoAngle(x:Int, y:Int, ang:Double, scale:Float):Pair<Int,Int>{
        val xv:Int  = (x/ scale.coerceAtMost(MAX_SCALE)).toInt()
        val yv:Int  = (y/scale.coerceAtMost(MAX_SCALE)).toInt()

        val sine    = sin(Math.toRadians(ang)).toFloat()
        val cosine  = cos(Math.toRadians(ang)).toFloat()
        return Pair(((xv*cosine)+(yv*sine)).toInt(), ((yv*cosine)-(xv*sine)).toInt())
    }
    override fun angleBetweenLines(
        ratio:Int,
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
        return -angle/ratio
    }

    /* val dspMin = Math.min(Resources.getSystem().displayMetrics.widthPixels,
                   Resources.getSystem().displayMetrics.heightPixels)*/

    /*  fun getPrvRto(w:Int,h:Int,scle:Float):Float{
          val lrgr=((if(w<h)h else w).toFloat()
                  /if(scle>1) scle else 1.toFloat())
          return Math.min((dspMin.toFloat() / lrgr), 1.toFloat())
      }*/
}