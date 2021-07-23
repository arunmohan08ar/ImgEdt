package com.tsehsrah.imageops.imageOperations.operators.implementations

import com.tsehsrah.imageops.imageOperations.di.ServiceLocator
import com.tsehsrah.imageops.imageOperations.models.IOperationParameters
import com.tsehsrah.imageops.imageOperations.operators.IImageOperator
import com.tsehsrah.imageops.imageOperations.utilities.ICPPFunctions

class DynamicRangeOperator: IImageOperator {
    private val sL=ServiceLocator
    private val cpp: ICPPFunctions = sL.getCPPFunctions()

    override fun initOperator(opParams:IOperationParameters){
        super.initOperator( opParams)
        opParams.secondaryBmp=null
    }

    override suspend fun perform(params: IOperationParameters) {
        params.tool?.let { tool ->
            params.rendered?.let {bmp->
                cpp.dRProcJNI(
                    bmp,
                    tool.getFrom(),
                    tool.getTo(),
                    params.noOfFreeCores
                )
            }
        }
    }

}
