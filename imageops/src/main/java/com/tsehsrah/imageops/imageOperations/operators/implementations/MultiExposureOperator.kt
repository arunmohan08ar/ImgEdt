package com.tsehsrah.imageops.imageOperations.operators.implementations

import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.dependancymanagement.ServiceLocator
import com.tsehsrah.imageops.imageOperations.models.ICache
import com.tsehsrah.imageops.imageOperations.models.IOperationParameters
import com.tsehsrah.imageops.imageOperations.operators.IImageOperator
import com.tsehsrah.imageops.imageOperations.utilities.IBitmapUtilities
import com.tsehsrah.imageops.imageOperations.utilities.ICPPFunctions
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class MultiExposureOperator : IImageOperator {
    private val sL=ServiceLocator
    private val cache: ICache = sL.getCache()
    private val bitmapUtils: IBitmapUtilities = sL.getBitmapUtils()
    private val cpp: ICPPFunctions = sL.getCPPFunctions()
    override fun initOperator( opParams: IOperationParameters) {
        super.initOperator(opParams)
        opParams.manager.loadSecondaryBmp()
        refresh()
    }

    override suspend fun perform(params: IOperationParameters) {
        params.rendered?.let {bmp->
            coroutineScope {
                val deferred = listOf(
                    async {
                        try {
                            bitmapUtils.validateRefCache(params)
                        }catch (oom:OutOfMemoryError){
                            params.manager.exceptionNotifier(vme=oom)
                        }
                    },async {
                        try{
                            bitmapUtils.validateSecCache(params)
                        }catch (oom:OutOfMemoryError){
                            params.manager.exceptionNotifier(vme=oom)
                        }
                    }
                )
                deferred.awaitAll()
                val rBmp=when(params.referenceMode){
                    ReferenceModes.INDEPENDENT->{
                        cache.ref?.getBmp()
                    }ReferenceModes.SECONDARY->{
                        cache.rX=cache.sX
                        cache.rY=cache.sY
                        cache.sec?.getBmp()
                    }else-> {
                        bmp
                    }
                }
                params.tool?.let{tool->
                    rBmp?.let { ref->
                        cache.sec?.getBmp()?.let { sec ->
                            cpp.eXP2ProcJNI(
                                bmp,
                                ref,
                                sec,
                                tool.getFrom(),
                                tool.getTo(),
                                cache.rX,
                                cache.rY,
                                cache.sX,
                                cache.sY,
                                tool.getIntensityFF(),
                                params.noOfFreeCores
                            )
                        }
                    }
                }
            }
        }
    }

    override fun refresh() {
        cache.clearAll()
    }

}