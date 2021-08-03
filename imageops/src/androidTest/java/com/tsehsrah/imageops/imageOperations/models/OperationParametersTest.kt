package com.tsehsrah.imageops.imageOperations.models

import android.graphics.Bitmap
import com.tsehsrah.imageops.fakes.FakeOperationsManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations

class OperationParametersTest{

    @InjectMocks
    private val manager:IOperationManager=FakeOperationsManager

    private lateinit var params:IOperationParameters
    private lateinit var pBmp: Bitmap

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        params=OperationParameters(manager)
        pBmp = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888)
        params.primaryBmp=pBmp
    }

    @Test
    fun renderResetTest(){
        params.renderQuality=.4f
        params.rendered=null
        runBlocking {
            params.resetRendered(.5f)
        }
        assertNotNull(params.rendered)
    }

    @Test
    fun renderResetQualityUpdateTest(){
        params.renderQuality=.4f
        runBlocking {
            params.resetRendered(.5f)
        }
        assertEquals(.5f,params.renderQuality)
    }

    @Test
    fun renderResetQualityVerification(){
        runBlocking {
            params.resetRendered(.5f)
        }
        assertEquals(25,params.rendered?.height)
    }


}