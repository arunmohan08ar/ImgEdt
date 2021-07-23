package com.tsehsrah.imageops.imageOperations.di

import com.tsehsrah.imageops.imageOperations.configs.CONSTANTS
import com.tsehsrah.imageops.imageOperations.models.*
import com.tsehsrah.imageops.imageOperations.utilities.BitmapUtils
import com.tsehsrah.imageops.imageOperations.utilities.CPPfuns
import com.tsehsrah.imageops.imageOperations.utilities.IBitmapUtilities
import com.tsehsrah.imageops.imageOperations.utilities.ICPPFunctions

object ServiceLocator {

    fun getConstants()                  : CONSTANTS             = CONSTANTS
    fun getNewImageCache()              : IImageCache           = ImageCache()
    fun getCache()                      : ICache                = Cache
    fun getNewToolStatus()              : IToolsStatus          = ToolsStatus()
    fun getToolStatusCopy(tool:IToolsStatus): IToolsStatus      = ToolsStatus(tool)


    fun getNewImageParameters()         : IImageParameters      = ImageParameters()
    fun getNewGestureStatus()           : IGestureStatus        = GestureStatus()

    fun getNewOperationParameters(manager: IOperationManager)
                                        : IOperationParameters  = OperationParameters(manager)
    fun getBitmapUtils()                : IBitmapUtilities      = BitmapUtils
    fun getCPPFunctions()               : ICPPFunctions         = CPPfuns
    fun getOperationError(e:Exception?,ve:VirtualMachineError?,s:String?)
                                        : IErrors               = OperationError(e,ve,s)

}