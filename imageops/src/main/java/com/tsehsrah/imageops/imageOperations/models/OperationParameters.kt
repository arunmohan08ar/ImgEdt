package com.tsehsrah.imageops.imageOperations.models

import android.graphics.Bitmap
import com.tsehsrah.imageops.imageOperations.configs.CONSTANTS.DEFAULT_FREE_CORE_COUNT
import com.tsehsrah.imageops.imageOperations.configs.CONSTANTS.DEFAULT_IMAGE_PARAMS
import com.tsehsrah.imageops.imageOperations.configs.CONSTANTS.DEFAULT_RENDER_QUALITY
import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.dependancymanagement.ServiceLocator
import com.tsehsrah.imageops.imageOperations.utilities.IBitmapUtilities

class OperationParameters(
    override val manager: IOperationManager,
    override var primaryBmp       : Bitmap?          = null,
    override var referenceBmp     : Bitmap?          = null,
    override var secondaryBmp     : Bitmap?          = null,
    override var rendered         : Bitmap?          = null,
    override var renderQuality    : Float            = DEFAULT_RENDER_QUALITY,
    override var tool             : IToolsStatus?    = null,
    override var refImgParams     : IImageParameters = DEFAULT_IMAGE_PARAMS,
    override var secImgParams     : IImageParameters = DEFAULT_IMAGE_PARAMS,
    override var referenceMode    : ReferenceModes   = ReferenceModes.PRIMARY,
    override var noOfFreeCores    : Int              = DEFAULT_FREE_CORE_COUNT

) : IOperationParameters {
    private var renderCache      : IImageCache?     = null
    private val bitmapUtils      : IBitmapUtilities = ServiceLocator.getBitmapUtils()

    override fun setToolStatus(tool:IToolsStatus){
        this.tool=tool
    }
    override fun clearRendered(){
        renderCache   = null
    }

    override fun clearAll() {
        primaryBmp     = null
        referenceBmp   = null
        secondaryBmp   = null
        rendered       = null
        renderCache    = null
        renderQuality  = DEFAULT_RENDER_QUALITY
        tool           = null
        refImgParams   = DEFAULT_IMAGE_PARAMS
        secImgParams   = DEFAULT_IMAGE_PARAMS
    }

    override fun resetReferenceParameters(){
        refImgParams   = DEFAULT_IMAGE_PARAMS
    }

    override suspend fun finalize(quality:Float) {
        primaryBmp?.let {bmp->
            renderQuality   = quality
            primaryBmp      = bitmapUtils.getScaledBmp(bmp,renderQuality)
            rendered        = primaryBmp
        }
    }


    override suspend fun resetRendered(quality: Float)  {
        try {
            renderQuality = quality
            renderCache?.let { cache ->
                if (!cache.isValid(quality)) {
                    primaryBmp?.let { bmp ->
                        renderCache = ImageCache(quality = renderQuality).setBmp(
                            bitmapUtils.getScaledBmp(
                                bmp,
                                renderQuality
                            )
                        )
                    }
                }
            } ?: let {
                primaryBmp?.let { bmp ->
                    renderCache = ImageCache(quality = renderQuality).setBmp(
                        bitmapUtils.getScaledBmp(
                            bmp,
                            renderQuality
                        )
                    )
                }
            }
            rendered = renderCache?.getBmp()
                ?.copy(renderCache?.getBmp()?.config ?: Bitmap.Config.ARGB_8888, true)
        }catch (oom:OutOfMemoryError){
            manager.exceptionNotifier(vme=oom)
        }
    }
}