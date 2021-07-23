package com.tsehsrah.imageops.imageOperations.operators

import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.models.IOperationParameters

interface IImageOperator  {
    fun initOperator(opParams:IOperationParameters){
        if(opParams.referenceMode==ReferenceModes.INDEPENDENT){
            opParams.manager.loadReferenceBmp()
        }
    }

    suspend fun perform(params: IOperationParameters)

    fun refresh(){}
}