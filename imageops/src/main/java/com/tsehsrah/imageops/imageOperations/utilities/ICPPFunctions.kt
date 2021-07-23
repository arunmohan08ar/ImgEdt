package com.tsehsrah.imageops.imageOperations.utilities

import android.graphics.Bitmap

interface ICPPFunctions {

    fun eXP2ProcJNI(nbm    : Bitmap,
                             reference: Bitmap,
                             exposed: Bitmap,
                             frm    :Int,
                             to     :Int,
                             rX     :Int,
                             rY     :Int,
                             sX     :Int,
                             sY     :Int,
                             intensity:Int,
                             noc    :Int
    )
    fun dRProcJNI(nbm: Bitmap,
                           frm    :Int,
                           to     :Int,
                           noc    :Int
    )
    fun bINProcJNI( nbm    : Bitmap,
                    ref    : Bitmap,
                    frm    : Int,
                    to     : Int,
                    x      : Int,
                    y      : Int,
                    noc    : Int
    )
    fun iNVProcJNI(nbm  : Bitmap,
                   ref  : Bitmap,
                   frm  : Int,
                   to   : Int,
                   x    : Int,
                   y    : Int,
                   noc  : Int
    )

}