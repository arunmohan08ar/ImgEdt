package com.tsehsrah.imageops.imageOperations.configs

import com.tsehsrah.imageops.imageOperations.operators.IImageOperator

interface IImageOperatorFactory{
    fun getOperator(mode: ImageOperators): IImageOperator
}