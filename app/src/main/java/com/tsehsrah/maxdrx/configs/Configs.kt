package com.tsehsrah.maxdrx.configs

import android.graphics.Bitmap

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
