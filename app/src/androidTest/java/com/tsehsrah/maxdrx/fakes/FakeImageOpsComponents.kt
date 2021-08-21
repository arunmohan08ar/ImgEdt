package com.tsehsrah.maxdrx.fakes

import com.tsehsrah.imageops.imageOperations.configs.IImageOperatorFactory
import com.tsehsrah.imageops.imageOperations.configs.ImageOperators
import com.tsehsrah.imageops.imageOperations.models.IOperationParameters
import com.tsehsrah.imageops.imageOperations.operators.IImageOperator

class FakeImageOperatorFactory:IImageOperatorFactory{
    private var fakeImageOperator:IImageOperator=FakeImageOperator()
    override fun getOperator(mode: ImageOperators): IImageOperator {
        return fakeImageOperator
    }

}
class FakeImageOperator:IImageOperator {
    override suspend fun perform(params: IOperationParameters) {
        performWasCalled()
    }
    fun performWasCalled(){

    }

    override fun initOperator(opParams: IOperationParameters) {
        if(opParams.tool?.getMode()!=ImageOperators.Expose)
            opParams.secondaryBmp=null
    }
}