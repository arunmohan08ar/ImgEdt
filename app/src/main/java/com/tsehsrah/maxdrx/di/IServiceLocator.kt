package com.tsehsrah.maxdrx.di

import com.tsehsrah.imageops.imageOperations.models.*
import com.tsehsrah.maxdrx.AdsManager
import com.tsehsrah.maxdrx.Analytics
import com.tsehsrah.maxdrx.models.*
import com.tsehsrah.maxdrx.utilities.*

interface IServiceLocator {
    //Local
    fun getItemImageSelectList()    : IItemImageSelectList
    fun getNewReferenceValues()     : IRefVal
    fun getNewLayerState()          : ILayerStates

    //utilities
    fun getImageSelectUtilities()   : IImageSelectUtility
    fun getImageUtilities()         : IImageUtilities
    fun getFilePrecheckUtilities()  : IFilePrechecks
    fun getImageFileUtilities()     : IImageFileUtilities
    fun getProcessMonitor()         : IProcessMonitor


    //imageOps
    fun getNewImageParameters()               : IImageParameters
    fun getNewToolStatus()                    : IToolsStatus
    fun getToolStatusCopy(tool: IToolsStatus) : IToolsStatus
    fun getNewGestureStatus()                 : IGestureStatus
    fun getNewOperationParameters(manager: IOperationManager)
                                              : IOperationParameters


    //
    fun getAdsManager()                       : AdsManager
    fun getAnalytics()                        : Analytics


}