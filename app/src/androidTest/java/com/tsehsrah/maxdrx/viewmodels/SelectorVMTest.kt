package com.tsehsrah.maxdrx.viewmodels

import android.app.Application
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.LargeTest
import com.tsehsrah.maxdrx.MainCoroutineRule
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.fakes.FakeImageRepo
import com.tsehsrah.maxdrx.fakes.FakeServiceLocator
import com.tsehsrah.maxdrx.getOrAwaitValue
import com.tsehsrah.maxdrx.models.IItemImageSelectList
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@LargeTest
class SelectorVMTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private var application: Application = mockk()
    private var sL: IServiceLocator? = null
    private var imgRepo : FakeImageRepo? = null
    private var sVM: SelectorVM? = null
    @Before
    fun setUp() {
        sL          = spyk(FakeServiceLocator())
        imgRepo     = spyk(FakeImageRepo())
        sL?.let {sl-> imgRepo?.let {repo->
            sVM         = SelectorVM(application, sl, repo)
            }
        }
    }

    @Test
    fun setExpectNewFileSetsReqImageSave(){
        sVM?.setExpectNewFile(8)
        val v=sVM?.reqAddImage?.getOrAwaitValue()
        assertEquals(true,v)
    }

    @Test
    fun resetImageRequestTest(){
        sVM?.resetImageRequest()
        val v=sVM?.reqAddImage?.getOrAwaitValue()
        assertEquals(false,v)
    }

    @Test
    fun addNewWorkFilesTest(){
        val uri: Uri =mockk()
        sVM?.addNewWorkFiles(uri)
        verify { imgRepo?.addNewWrkFiles(any(),any(),uri) }
    }

    @Test
    fun setCurrentPosTest(){
        val pos=0
        val mockImgItm=mockk<IItemImageSelectList>()
        every { mockImgItm.thumpBmp  } returns mockk()
        sVM?.imageSelectList?.value?.add(mockImgItm)
        sVM?.setCurrentPos(pos)
        verify { imgRepo?.setCurrentPos(any(),pos,false) }
    }
    @Test
    fun setReferencePosTest(){
        val pos=0
        val mockImgItm=mockk<IItemImageSelectList>()
        every { mockImgItm.thumpBmp  } returns mockk()
        sVM?.imageSelectList?.value?.add(mockImgItm)
        sVM?.setReferencePos(pos)
        verify { imgRepo?.setReference(any(),pos) }
    }
    @Test
    fun setSecondaryPosTest(){
        val pos=0
        val mockImgItm=mockk<IItemImageSelectList>()
        every { mockImgItm.thumpBmp  } returns mockk()
        sVM?.imageSelectList?.value?.add(mockImgItm)
        sVM?.setSecondaryPos(pos)
        verify { imgRepo?.setSecondary(any(),pos) }
    }


    @Test
    fun deleteDataAtZeroTest(){
        sVM?.imageSelectList?.value?.add(mockk())
        sVM?.deleteDataAt(0)
        verify {
            imgRepo?.removeDataAtWasCalled()
        }
    }







}