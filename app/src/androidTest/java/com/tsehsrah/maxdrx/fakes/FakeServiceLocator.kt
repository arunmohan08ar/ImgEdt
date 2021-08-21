package com.tsehsrah.maxdrx.fakes

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.test.platform.app.InstrumentationRegistry
import com.tsehsrah.imageops.imageOperations.configs.IImageOperatorFactory
import com.tsehsrah.imageops.imageOperations.models.*
import com.tsehsrah.maxdrx.AdsManager
import com.tsehsrah.maxdrx.Analytics
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.models.*
import com.tsehsrah.maxdrx.utilities.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

class FakeServiceLocator: IServiceLocator  {
    //app
    override fun getAppContext(ctx: AndroidViewModel): Context = InstrumentationRegistry
                                                                    .getInstrumentation()
                                                                    .context
        @ExperimentalCoroutinesApi
        override fun getIODispatcher(): CoroutineDispatcher = Dispatchers.IO
        @ExperimentalCoroutinesApi
        override fun getDefaultDispatcher(): CoroutineDispatcher = TestCoroutineDispatcher()
        //Local
        override fun getItemImageSelectList()    : IItemImageSelectList = ItemImageSelectList()
        override fun getNewReferenceValues()     : IRefVal = RefVal()
        override fun getNewLayerState()          : ILayerStates = LayerState(this)

        //utilities
        override fun getImageSelectUtilities()   : IImageSelectUtility = ImgSelectUtils
        override fun getImageUtilities()         : IImageUtilities = ImageUtilities
        override fun getFilePrecheckUtilities()  : IFilePrechecks = FlePreChecks
        override fun getImageFileUtilities()     : IImageFileUtilities = FakeImageFileUtils()
        override fun getProcessMonitor()         : IProcessMonitor = ProcessMonitor



        //imageOps
        override fun getImageOperatorFactory()           : IImageOperatorFactory = FakeImageOperatorFactory()

        override fun getNewImageParameters()             : IImageParameters =ImageParameters()
        override fun getNewToolStatus()                  : IToolsStatus = ToolsStatus()
        override fun getToolStatusCopy(tool: IToolsStatus): IToolsStatus = tool
        override fun getNewGestureStatus()               : IGestureStatus = GestureStatus()


        private var opParameters:IOperationParameters?=null
        override fun getNewOperationParameters(manager: IOperationManager)
                : IOperationParameters {
            return opParameters ?:run{
                val v =OperationParameters(manager)
                v.primaryBmp= Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888)
                opParameters=v
                v
            }
        }

        //
        override fun getAdsManager(): AdsManager = AdsManager
        override fun getAnalytics(): Analytics = Analytics
    }


