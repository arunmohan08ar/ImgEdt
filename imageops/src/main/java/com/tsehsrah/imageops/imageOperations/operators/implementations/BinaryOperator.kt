package com.tsehsrah.imageops.imageOperations.operators.implementations

import com.tsehsrah.imageops.imageOperations.dependancymanagement.ServiceLocator
import com.tsehsrah.imageops.imageOperations.models.ICache

import com.tsehsrah.imageops.imageOperations.models.IOperationParameters
import com.tsehsrah.imageops.imageOperations.operators.IImageOperator
import com.tsehsrah.imageops.imageOperations.utilities.IBitmapUtilities
import com.tsehsrah.imageops.imageOperations.utilities.ICPPFunctions


class BinaryOperator : IImageOperator {
    private val sL=ServiceLocator
    private val cache:ICache=ServiceLocator.getCache()
    private val bitmapUtils:IBitmapUtilities=sL.getBitmapUtils()
    private val cpp:ICPPFunctions=sL.getCPPFunctions()

    override fun initOperator(opParams:IOperationParameters) {
        super.initOperator(opParams)
        opParams.secondaryBmp=null
    }

    override suspend fun perform(params: IOperationParameters) {
        params.tool?.let { tool ->
            params.rendered?.let {bmp->
                try {
                    bitmapUtils.validateRefCache(params)
                }catch (oom:OutOfMemoryError){
                    params.manager.exceptionNotifier(vme=oom)
                }
                val ref=cache.ref?.getBmp()?:bmp
                cpp.bINProcJNI(
                    bmp,
                    ref,
                    tool.getFrom(),
                    tool.getTo(),
                    cache.rX,
                    cache.rY,
                    params.noOfFreeCores
                )
            }
        }
    }
}
