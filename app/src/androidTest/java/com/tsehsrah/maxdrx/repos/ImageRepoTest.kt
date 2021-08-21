package com.tsehsrah.maxdrx.repos

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.LargeTest
import com.tsehsrah.maxdrx.MainCoroutineRule
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.fakes.FakeImageFileUtils
import com.tsehsrah.maxdrx.fakes.FakeServiceLocator
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@LargeTest
class ImageRepoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private lateinit var ctx: Context
    private var sL: IServiceLocator? = null
    private val fileUtils= spyk(FakeImageFileUtils())
    private lateinit var imgRepo:IImageRepository
    @Before
    fun setUp() {
        ctx=mockk()
        sL= spyk(FakeServiceLocator())
        every { sL?.getImageFileUtilities() }returns fileUtils
        sL?.let {
            imgRepo=ImageRepo(it)
        }
    }

    @Test
    fun setCurrentPosTest()= runBlocking{
        val pos=8
        imgRepo.setCurrentPos(ctx,pos,true)
        delay(100)
        val v=imgRepo.currentBmpSF.value
        assertNotEquals(null,v)
    }


    @Test
    fun setReferenceTest()= runBlocking{
        imgRepo.setReference(ctx,0)
        delay(500)
        val v=imgRepo.referenceImageSF.value
        assertNotEquals(null,v)
    }

    @Test
    fun setSecondaryTest()= runBlocking{
        imgRepo.setSecondary(ctx,0)
        delay(500)
        val v=imgRepo.secondaryImageSF.value
        assertNotEquals(null,v)
    }

    @Test
    fun addNewWorkFilesSavesImageAndThumpTest()= runBlocking{
        imgRepo.addNewWrkFiles(ctx,0, mockk())
        delay(500)
        verify (atLeast= 2){ fileUtils.saveWorkBmp(any(),ctx,any(),any()) }
    }


    @Test
    fun saveImageToGalleryTest()= runBlocking{
        imgRepo.saveImageToGallery(mockk(),ctx, mockk())
        verify{ fileUtils.saveImageToGallery(any(),ctx,any(),any()) }
    }

    @Test
    fun discardSecondaryImageTest(){
        imgRepo.discardSecondaryImage()
        val v=imgRepo.secondaryImageSF.value
        assertEquals(null,v)
    }

    @Test
    fun discardReferenceImageTest(){
        imgRepo.discardReferenceImage()
        val v=imgRepo.referenceImageSF.value
        assertEquals(null,v)
    }

    @Test
    fun discardCurrentImageTest(){
        imgRepo.discardCurrentImage()
        val v=imgRepo.currentBmpSF.value
        assertEquals(null,v)
    }

    @Test
    fun removeDataAtTest()= runBlocking{
        imgRepo.removeDataAt(ctx,0,0,mockk())
        delay(500)
        verify(atLeast = 2) { fileUtils.removeWorkBmp(any(),any(),any()) }
    }


    @Test
    fun clearAllWorkDataTest()= runBlocking{
        imgRepo.clearAllWorkData(ctx)
        delay(500)
        verify { fileUtils.clearWorkDirectory(ctx,any()) }
        assertEquals(null ,imgRepo.currentBmpSF.value)
        assertEquals(null ,imgRepo.referenceImageSF.value)
        assertEquals(null ,imgRepo.secondaryImageSF.value)
        assertEquals(null ,imgRepo.workDirectoryUpdated.value)
    }

    @Test
    fun getBmpTest()= runBlocking{
        val v=imgRepo.getBmp(ctx,0,true)
        assertEquals(fileUtils.bmp,v)
    }


}