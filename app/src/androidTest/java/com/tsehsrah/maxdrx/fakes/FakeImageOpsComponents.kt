package com.tsehsrah.maxdrx.fakes

import com.tsehsrah.imageops.imageOperations.models.IOperationParameters
import com.tsehsrah.imageops.imageOperations.operators.IImageOperator

class FakeImageOpsComponents:IImageOperator {
    override suspend fun perform(params: IOperationParameters) {

    }
}