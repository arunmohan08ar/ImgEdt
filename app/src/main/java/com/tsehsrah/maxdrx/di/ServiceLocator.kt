package com.tsehsrah.maxdrx.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.tsehsrah.imageops.imageOperations.configs.IImageOperatorFactory
import com.tsehsrah.imageops.imageOperations.configs.ImageOperatorFactory
import com.tsehsrah.imageops.imageOperations.dependancymanagement.ServiceLocator
import com.tsehsrah.imageops.imageOperations.models.*
import com.tsehsrah.maxdrx.AdsManager
import com.tsehsrah.maxdrx.Analytics
import com.tsehsrah.maxdrx.models.*
import com.tsehsrah.maxdrx.utilities.*

object ServiceLocator :IServiceLocator{
    //app
    override fun getAppContext(ctx:AndroidViewModel): Context        = ctx.getApplication<Application>()
                                                                        .applicationContext

    //Local
    override fun getItemImageSelectList()    : IItemImageSelectList  = ItemImageSelectList()
    override fun getNewReferenceValues()     : IRefVal               = RefVal()
    override fun getNewLayerState()          : ILayerStates          = LayerState(this)

    //utilities
    override fun getImageSelectUtilities()   : IImageSelectUtility   = ImgSelectUtils
    override fun getImageUtilities()         : IImageUtilities       = ImageUtilities
    override fun getFilePrecheckUtilities()  : IFilePrechecks        = FlePreChecks
    override fun getImageFileUtilities()     : IImageFileUtilities   = FileUtilsL
    override fun getProcessMonitor()         : IProcessMonitor       = ProcessMonitor


    //imageOps
    override fun getImageOperatorFactory()           : IImageOperatorFactory = ImageOperatorFactory

    override fun getNewImageParameters()             : IImageParameters      = ServiceLocator
                                                                        .getNewImageParameters()
    override fun getNewToolStatus()                  : IToolsStatus          = ServiceLocator
                                                                        .getNewToolStatus()
    override fun getToolStatusCopy(tool:IToolsStatus): IToolsStatus          = ServiceLocator
                                                                        .getToolStatusCopy(tool)
    override fun getNewGestureStatus()               : IGestureStatus        = ServiceLocator
                                                                        .getNewGestureStatus()
    override fun getNewOperationParameters(manager: IOperationManager)
                                                     : IOperationParameters  = ServiceLocator
                                                                        .getNewOperationParameters(manager)


    //
    override fun getAdsManager():AdsManager =AdsManager
    override fun getAnalytics(): Analytics =Analytics


}