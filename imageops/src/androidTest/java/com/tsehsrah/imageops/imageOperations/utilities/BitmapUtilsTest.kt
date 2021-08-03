package com.tsehsrah.imageops.imageOperations.utilities

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tsehsrah.imageops.fakes.FakeOperationsManager
import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.models.*
import com.tsehsrah.imageops.imageOperations.utilities.BitmapUtils.getScaledBmp
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks

import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class BitmapUtilsTest2{
    private val size=50

    private lateinit var bmp: Bitmap
    private lateinit var params: IOperationParameters

    @InjectMocks
    private val utilities: IBitmapUtilities = BitmapUtils

    @InjectMocks
    private val manager: IOperationManager = FakeOperationsManager

    @InjectMocks
    private val cache: ICache =Cache

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        bmp=Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888)
    }
    @After
    fun tearDown(){
        cache.ref=null
        cache.sec=null
    }


    //scale
    @Test
    fun scaleTest(){
        val scale=3f
        val rbmb= runBlocking { getScaledBmp(bmp,scale)}
        Assert.assertEquals((size*scale).toInt(),rbmb.width)
    }
    @Test
    fun scaleTestDownScale(){
        val scale=.5f
        val rbmb= runBlocking { getScaledBmp(bmp,scale)}
        Assert.assertEquals((size*scale).toInt(),rbmb.width)
    }

    //reference cache validation
    @Test
    fun nonNullSetsRefCache(){
        cache.ref=null
        params=OperationParameters(manager)
        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        runBlocking {
            utilities.validateRefCache(params)
        }
        Assert.assertNotNull(cache.ref)
    }
    @Test
    fun nonNullSetsRefCacheBmp(){
        cache.ref=null
        params=OperationParameters(manager)
        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        runBlocking {
            utilities.validateRefCache(params)
        }
        Assert.assertNotNull(cache.ref?.getBmp())
    }
    @Test
    fun nonNullRefSetsXY(){
        params=OperationParameters(manager)
        params.refImgParams.scale=2f
        params.refImgParams.scrollX=3
        params.refImgParams.scrollY=4
        params.renderQuality=5f

        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        runBlocking {
            utilities.validateRefCache(params)
        }
        Assert.assertEquals(cache.rX,(2*3*5))
        Assert.assertEquals(cache.rY,(2*4*5))
    }
    @Test
    fun primaryModeRefSetsNullToCache(){
        params=OperationParameters(manager)
        params.referenceMode=ReferenceModes.PRIMARY
        runBlocking {
            utilities.validateRefCache(params)
        }
        Assert.assertEquals(cache.ref,null)
    }
    @Test
    fun secondaryModeRefSetsNullToCache(){
        params=OperationParameters(manager)
        params.referenceMode=ReferenceModes.SECONDARY
        runBlocking {
            utilities.validateRefCache(params)
        }
        Assert.assertEquals(cache.ref,null)
    }


    //secondary cache validation
    @Test
    fun nonNullSetsSecCache(){
        cache.sec=null
        params=OperationParameters(manager)
        params.secondaryBmp=bmp
        runBlocking {
            utilities.validateSecCache(params)
        }
        Assert.assertNotNull(cache.sec)
    }
    @Test
    fun nonNullSetsSecCacheBmp(){
        cache.sec=null
        params=OperationParameters(manager)
        params.secondaryBmp=bmp
        runBlocking {
            utilities.validateSecCache(params)
        }
        Assert.assertNotNull(cache.sec?.getBmp())
    }
    @Test
    fun nonNullSecSetsXY(){
        params=OperationParameters(manager)
        params.secImgParams.scale=2f
        params.secImgParams.scrollX=3
        params.secImgParams.scrollY=4
        params.renderQuality=5f

        params.secondaryBmp=bmp
        runBlocking {
            utilities.validateSecCache(params)
        }
        Assert.assertEquals(cache.sX,(2*3*5))
        Assert.assertEquals(cache.sY,(2*4*5))
    }

}