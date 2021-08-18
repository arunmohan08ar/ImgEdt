package com.tsehsrah.maxdrx.di

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.tsehsrah.imageops.imageOperations.configs.IImageOperatorFactory
import com.tsehsrah.imageops.imageOperations.models.*
import com.tsehsrah.maxdrx.AdsManager
import com.tsehsrah.maxdrx.Analytics
import com.tsehsrah.maxdrx.models.*
import com.tsehsrah.maxdrx.utilities.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface IServiceLocator {
    //app
    fun getAppContext(ctx:AndroidViewModel): Context
    fun getIODispatcher():CoroutineDispatcher
    fun getDefaultDispatcher():CoroutineDispatcher

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
    fun getImageOperatorFactory()             : IImageOperatorFactory
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