package com.tsehsrah.imageops.imageOperations.configs

import com.tsehsrah.imageops.imageOperations.models.IImageParameters
import com.tsehsrah.imageops.imageOperations.models.ImageParameters

object CONSTANTS {
    val DEFAULT_IMAGE_PARAMS: IImageParameters = ImageParameters()
    const val DEFAULT_RENDER_QUALITY:Float= 0.5F
    val DEFAULT_TOOL_MODE:ImageOperators=ImageOperators.DynamicRange
    const val DEFAULT_FREE_CORE_COUNT:Int=8
}