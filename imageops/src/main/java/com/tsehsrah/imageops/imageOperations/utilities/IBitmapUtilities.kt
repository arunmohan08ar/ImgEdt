package com.tsehsrah.imageops.imageOperations.utilities

import android.graphics.Bitmap
import com.tsehsrah.imageops.imageOperations.models.IOperationParameters

interface IBitmapUtilities {
    suspend fun getScaledBmp(bmp: Bitmap, ratio: Float): Bitmap
    suspend fun validateRefCache(params: IOperationParameters)
    suspend fun validateSecCache(params: IOperationParameters)
    }