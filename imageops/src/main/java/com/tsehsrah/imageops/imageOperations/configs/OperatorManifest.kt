package com.tsehsrah.imageops.imageOperations.configs

import com.tsehsrah.imageops.imageOperations.models.IOperatorDescription
import com.tsehsrah.imageops.imageOperations.models.OperatorDescription
import com.tsehsrah.imageops.imageOperations.operators.IImageOperator
import com.tsehsrah.imageops.imageOperations.operators.implementations.InvertOperator
import com.tsehsrah.imageops.imageOperations.operators.implementations.BinaryOperator
import com.tsehsrah.imageops.imageOperations.operators.implementations.DynamicRangeOperator
import com.tsehsrah.imageops.imageOperations.operators.implementations.MultiExposureOperator

enum class ImageOperators(val operator: IOperatorDescription) {
    DynamicRange(OperatorDescription(
        "Dynamic Range",
        "Redefine the dynamic range by stretching the specified region into maximum" +
                " available range to bring forward the contrast over what matters most.",
        false)
    ),
    Binary(OperatorDescription(
        "Binary",
        "Transform the image into a bi colour art.",
        false)
    ),
    Invert(OperatorDescription(
        "Invert",
        "Invert the colours based on dynamic range.",
        false)
    ),
    Expose(OperatorDescription(
        "Expose",
        "Create stunning multi exposure effects. but from now on, differently.",
        true)
    )
}

object ImageOperatorFactory :IImageOperatorFactory  {
    override fun getOperator(mode: ImageOperators): IImageOperator {
        return when (mode) {
            ImageOperators.Binary -> {
                BinaryOperator()
            }
            ImageOperators.Invert -> {
                InvertOperator()
            }
            ImageOperators.Expose -> {
                MultiExposureOperator()
            }
            else -> {
                DynamicRangeOperator()
            }
        }
    }
}


enum class ReferenceModes{
    PRIMARY,
    SECONDARY,
    INDEPENDENT
}


