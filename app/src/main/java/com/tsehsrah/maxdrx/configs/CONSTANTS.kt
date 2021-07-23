package com.tsehsrah.maxdrx.configs

import android.graphics.Color


object CONSTANTS{
    const val IMG_REQ_CODE=118
    const val INVALID:Int =-1
    const val BLANK:String=""
    const val IMG_OP_DIR="Tsehsrah/MaxDRX"
    const val IMG_TMP_DIR="Tsehsrah/MaxDRX/temp/"
    const val IMG_SVE_PATH_REL= "Pictures/"

    const val TEMP_IMG_NAME="temp"
    const val THUMP_IMG_NAME="thump"

    const val NO_NAME="Error"

    const val IMAGE_SELECT_THUMP_SIZE=80


    val LAYER_SELECTED_COLOUR=Color.rgb(150,150,150)
    const val TRANSPARENT=Color.TRANSPARENT

    const val MONITOR_ENTRY_CAPACITY=15
    const val QUAD_TAP_DELAY:Long =600
    val DEFAULT_SAVE_FORMAT:String= ImageFormats.PNG.s

    //preferences
    const val PREF_SETTINGS:String="settings"
    const val PREF_PREV_QUALITY:String="preview_quality"
    const val PREF_IMAGE_FORMAT:String="save_format"
    const val PREF_AUTO_SWITCH:String ="auto_switch"

    //rendering
    const val DEFAULT_PREVIEW_QUALITY:Float= 0.4f
    const val RENDERING_MAX_TIME=180
    const val RENDERING_MIN_TIME=40
    const val RENDERING_TIME_ANOMALY_COUNT_THRESHOLD=5
    const val MAX_SCALE:Float = 5.0f
    const val PREVIEW_MIN_QUALITY=.04f
    const val PREVIEW_QUALITY_REDUCTION_RATIO=.8F
}
