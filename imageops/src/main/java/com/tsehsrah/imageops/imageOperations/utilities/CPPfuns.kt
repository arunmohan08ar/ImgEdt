package com.tsehsrah.imageops.imageOperations.utilities
import android.graphics.Bitmap
object CPPfuns : ICPPFunctions {
    init {
        System.loadLibrary("proc_lib")
    }


    external override fun eXP2ProcJNI(nbm    : Bitmap,
                                      reference:Bitmap,
                                      exposed: Bitmap,
                                      frm    : Int,
                                      to     : Int,
                                      rX     : Int,
                                      rY     : Int,
                                      sX     : Int,
                                      sY     : Int,
                                      intensity: Int,
                                      noc    : Int
                             )
    external override fun dRProcJNI(nbm     : Bitmap,
                                    frm     : Int,
                                    to      : Int,
                                    noc     : Int
                           )
    external override fun bINProcJNI(nbm    : Bitmap,
                                     ref    : Bitmap,
                                     frm    : Int,
                                     to     : Int,
                                     x      : Int,
                                     y      : Int,
                                     noc    : Int
                            )
    external override fun iNVProcJNI(nbm    : Bitmap,
                                     ref    : Bitmap,
                                     frm    : Int,
                                     to     : Int,
                                     x      : Int,
                                     y      : Int,
                                     noc    : Int
                            )


/*  for using class
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }*/

}