package com.tsehsrah.maxdrx.utilities

import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.ActivityResultLauncher
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.CONSTANTS.IMG_REQ_CODE
import com.tsehsrah.maxdrx.di.IServiceLocator

object ImgSelectUtils : IImageSelectUtility {
    override fun requestImage(ctx:Context,resultLauncher: ActivityResultLauncher<Intent>,sL:IServiceLocator) {
        if (sL.getFilePrecheckUtilities().checkFilePermisson(ctx, IMG_REQ_CODE)) {
            Toast.makeText(ctx,ctx.getText(R.string.select_image_toast),LENGTH_SHORT).show()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(intent)
        }
    }



}