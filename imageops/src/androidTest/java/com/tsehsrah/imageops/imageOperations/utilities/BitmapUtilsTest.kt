package com.tsehsrah.imageops.imageOperations.utilities

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tsehsrah.imageops.fakes.FakeOperationsManager
import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.models.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapUtilsTest{
    private val size=50
    private lateinit var bmp: Bitmap
    private lateinit var params: IOperationParameters
    private lateinit var cache: ICache

    private val utilities : IBitmapUtilities = BitmapUtils
    private val manager :IOperationManager = FakeOperationsManager

    @Before
    fun setup(){
        cache = Cache
        bmp=Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888)
        params=OperationParameters(manager)

    }
    @After
    fun tearDown(){
        cache.ref=null
        cache.sec=null
    }

    /***********************************************************************************************
     * Scale test
     **********************************************************************************************/

    @Test
    fun scaleTest(){
        val scale=3f
        val rbmb= runBlocking { utilities.getScaledBmp(bmp,scale)}
        Assert.assertEquals((size*scale).toInt(),rbmb.width)
    }
    @Test
    fun scaleTestDownScale(){
        val scale=.5f
        val rbmb= runBlocking { utilities.getScaledBmp(bmp,scale)}
        Assert.assertEquals((size*scale).toInt(),rbmb.width)
    }

    /***********************************************************************************************
    *  reference cache validation
    ***********************************************************************************************/
    @Test
    fun nonNullSetsRefCache(){
        cache.ref=null
        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        validateRefCache()
        Assert.assertNotNull(cache.ref)
    }
    @Test
    fun nonNullSetsRefCacheBmp(){
        cache.ref=null
        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        validateRefCache()
        Assert.assertNotNull(cache.ref?.getBmp())
    }
    @Test
    fun nonNullRefSetsXY(){
        params.refImgParams.scale=2f
        params.refImgParams.scrollX=3
        params.refImgParams.scrollY=4
        params.renderQuality=5f

        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        validateRefCache()
        Assert.assertEquals(cache.rX,(2*3*5))
        Assert.assertEquals(cache.rY,(2*4*5))
    }
    @Test
    fun primaryModeRefSetsNullToCache(){
        params.referenceMode=ReferenceModes.PRIMARY
        validateRefCache()
        Assert.assertEquals(cache.ref,null)
    }
    @Test
    fun secondaryModeRefSetsNullToCache(){
        params.referenceMode=ReferenceModes.SECONDARY
        validateRefCache()
        Assert.assertEquals(cache.ref,null)
    }
    @Test
    fun refCacheShouldValidateToSame(){
        cache.ref=null
        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        validateRefCache()
        val expected=cache.ref?.getBmp()
        params.referenceBmp=bmp.copy(bmp.config,false)
        validateRefCache()
        Assert.assertEquals(expected,cache.ref?.getBmp())
    }

    /**
     * verify that cache gets replaced by change in parameter values.
     * if any changed value invalidates and trigger update, invalidation causes update
     * */
    @Test
    fun refCacheShouldBeUpdatedWithQuality(){
        cache.ref=null
        params.referenceMode=ReferenceModes.INDEPENDENT
        params.referenceBmp=bmp
        validateRefCache()
        val expected=cache.ref?.getBmp()
        params.renderQuality+=1
        params.referenceBmp=bmp.copy(bmp.config,false)
        validateRefCache()
        Assert.assertNotEquals(expected,cache.ref?.getBmp())
    }

    private fun validateRefCache(){
        runBlocking {
            utilities.validateRefCache(params)
        }
    }


    /***********************************************************************************************
     * Secondary cache validation
     **********************************************************************************************/
    @Test
    fun nonNullSetsSecCache(){
        cache.sec=null
        params.secondaryBmp=bmp
        validateSecCache()
        Assert.assertNotNull(cache.sec)
    }
    @Test
    fun nonNullSetsSecCacheBmp(){
        cache.sec=null
        params.secondaryBmp=bmp
        validateSecCache()
        Assert.assertNotNull(cache.sec?.getBmp())
    }
    @Test
    fun nonNullSecSetsXY(){
        params.secImgParams.scale=2f
        params.secImgParams.scrollX=3
        params.secImgParams.scrollY=4
        params.renderQuality=5f

        params.secondaryBmp=bmp
        validateSecCache()
        Assert.assertEquals(cache.sX,(2*3*5))
        Assert.assertEquals(cache.sY,(2*4*5))
    }

    @Test
    fun secCacheShouldValidateToSame(){
        cache.sec=null
        params.secondaryBmp=bmp
        validateSecCache()
        val expected=cache.sec?.getBmp()
        params.secondaryBmp=bmp.copy(bmp.config,false)
        validateRefCache()
        Assert.assertEquals(expected,cache.sec?.getBmp())
    }

    /**
     * verify that cache gets replaced by change in parameter values.
     * if any changed value invalidates and trigger update, invalidation causes update
     * */
    @Test
    fun secCacheShouldBeUpdatedWithQuality(){
        cache.sec=null
        params.secondaryBmp=bmp
        validateSecCache()
        val expected=cache.sec?.getBmp()
        params.renderQuality+=1
        params.secondaryBmp=bmp.copy(bmp.config,false)
        validateSecCache()
        Assert.assertNotEquals(expected,cache.sec?.getBmp())
    }
    private fun validateSecCache(){
        runBlocking {
            utilities.validateSecCache(params)
        }
    }


}