package com.tsehsrah.maxdrx.configs

import android.graphics.Bitmap
import com.google.firebase.analytics.FirebaseAnalytics

enum class ImageLayers{
    PREVIEW,
    REFERENCE,
    SECONDARY
}

enum class ImageFormats(val s:String,val format:Bitmap.CompressFormat){
    JPEG("jpeg",Bitmap.CompressFormat.JPEG),
    PNG("png",Bitmap.CompressFormat.PNG),
    WEBP("webp",Bitmap.CompressFormat.WEBP)
}


enum class FBEvent(val type:String){
    NEW_FILE(FirebaseAnalytics.Event.SELECT_ITEM),
    OOM("OutOfMemoryError"),
    IOException("IOException"),
}

enum class EventAt{
    EDITOR_VM,SELECTOR_VM,EDITOR_ACT,NO_VAL
}