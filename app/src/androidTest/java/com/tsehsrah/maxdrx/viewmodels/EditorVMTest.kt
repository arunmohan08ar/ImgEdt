package com.tsehsrah.maxdrx.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.tsehsrah.imageops.imageOperations.configs.ImageOperators
import com.tsehsrah.imageops.imageOperations.configs.ReferenceModes
import com.tsehsrah.imageops.imageOperations.models.*

import com.tsehsrah.maxdrx.fakes.FakeImageRepo
import com.tsehsrah.maxdrx.MainCoroutineRule
import com.tsehsrah.maxdrx.configs.ImageLayers
import com.tsehsrah.maxdrx.di.IServiceLocator
import com.tsehsrah.maxdrx.fakes.FakeImageOperator
import com.tsehsrah.maxdrx.fakes.FakeServiceLocator
import com.tsehsrah.maxdrx.getOrAwaitValue
import com.tsehsrah.maxdrx.models.ILayerStates
import io.mockk.*

import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.lessThan
import org.junit.*

import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@LargeTest
@RunWith(AndroidJUnit4::class)
class EditorVMTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private var application: Application = mockk()
    private var sL: IServiceLocator? = null
    private var imgRepo :FakeImageRepo? = null
    private var eVM: EditorVM? = null
    private lateinit var layerState:ILayerStates


    @Before
    fun setUp() {
        sL          = spyk(FakeServiceLocator())
        imgRepo     = spyk(FakeImageRepo())
        layerState  = spyk(sL?.getNewLayerState()?:throw Exception("sl null @before "))
        every { sL?.getNewLayerState()?:throw Exception("@before 1") }returns layerState
        sL?.let {sl-> imgRepo?.let {repo->
            eVM         = EditorVM(application, sl, repo)
            }
        }
    }


    @Test
    fun toggleSelectionVisibilityTest() {
        eVM?:throw Exception("evm null")
        val v1 = eVM?.selectionFragmentVisibility?.getOrAwaitValue()?:throw Exception("1")
        eVM?.toggleSelectionVisibility()
        val v2 = eVM?.selectionFragmentVisibility?.getOrAwaitValue()?:throw Exception("2")
        Assert.assertNotEquals(v1, v2)
        eVM?.toggleSelectionVisibility()
        val v3 = eVM?.selectionFragmentVisibility?.getOrAwaitValue()?:throw Exception("3")
        Assert.assertEquals(v1, v3)
    }

    @Test
    fun setGestureStatusTest() {
        val gs: IGestureStatus = GestureStatus()
        eVM?.setGestureStatus(gs)?:throw Exception("evm null")
        val v = eVM?.gestureStatus?.getOrAwaitValue()?:throw Exception("1")
        Assert.assertEquals(v, gs)
    }


    @Test
    fun updateToolStatusLiveDataUpdateTest() = runBlockingTest {
        eVM?:throw Exception("evm null")
        val ts: IToolsStatus = ToolsStatus()
        eVM?.updateToolStatus(ts)?:throw Exception("1")
        val v = eVM?.toolsStatus?.value?:throw Exception("2")
        Assert.assertEquals(ts, v)
    }

    @Test
    fun updateToolStatusWithDifferentModesChangesMode()  {
        val ts: IToolsStatus = ToolsStatus()
        val operator = spyk(FakeImageOperator())
        sL?:throw Exception("sl null")
        val opFact=spyk(sL?.getImageOperatorFactory()?:throw Exception("1"))
        every { sL?.getImageOperatorFactory()?:throw Exception("2") } returns opFact
        every { opFact.getOperator(any()) } returns operator
        ts.setMode(ImageOperators.Invert)
        eVM?:throw Exception("evm null")
        eVM?.updateToolStatus(ts)?:throw Exception("evm null")
        verify { operator.initOperator(any()) }
    }

    @Test
    fun updateToolStatusWithSameModesDoesNotReInit() {
        val ts: IToolsStatus = ToolsStatus()
        val operator = spyk(FakeImageOperator())
        every { sL?.getImageOperatorFactory()?.getOperator(any())?:throw Exception("1") } returns operator
        ts.setMode(ImageOperators.Binary)
        eVM?:throw Exception("evm null")
        eVM?.updateToolStatus(ts)?:throw Exception("2")
        eVM?.updateToolStatus(ts)?:throw Exception("3")
        verify(atMost = 1) { operator.initOperator(any()) }
    }


    @Test
    fun applyChangesUpdatesHeadsUpStringTest() {
        eVM?:throw Exception("evm null")
        eVM?.applyChanges()?:throw Exception("1")
        Assert.assertNotEquals(lessThan(10),eVM?.userHeadsUpString?.value?:throw Exception("2"))
    }
    @Test
    fun applyChangesCallsSaveWorkImage() {
        eVM?:throw Exception("evm null")
        eVM?.applyChanges()?:throw Exception("2")
        coVerify { imgRepo?.sveWorkImage(any(),any(),any()) }
    }

    @Test
    fun updateImageParametersTest(){
        val ip:IImageParameters = ImageParameters()
        eVM?:throw Exception("evm null")
        eVM?.updateImageParameters(ip)
        val v=eVM?.layerParameters?.value?:throw Exception("1")
        Assert.assertEquals(ip,v)
    }
    @Test
    fun updateImageParametersCausesOperationPerform()  {
        val operator = spyk(FakeImageOperator())
        sL?:throw Exception("sl null")
        every { sL?.getImageOperatorFactory()?.getOperator(any())?:throw Exception("1") } returns operator
        val ip:IImageParameters = ImageParameters()
        val ts: IToolsStatus = ToolsStatus()

        ts.setMode(ImageOperators.Expose)
        eVM?.updateToolStatus(ts)?:throw Exception("2")
        ts.setMode(ImageOperators.DynamicRange)
        eVM?.updateToolStatus(ts)
        eVM?.updateImageParameters(ip)
        verify { operator.performWasCalled() }
    }



    @Test
    fun setActiveLayerChangesActiveLayerTest(){
        eVM?:throw Exception("evm null")
        val v=eVM?.layerParameters?.getOrAwaitValue()?:throw Exception("1")
        eVM?.setActiveLayer(ImageLayers.REFERENCE)
        verify { layerState.changeTo(v,ImageLayers.REFERENCE) }
    }
    @Test
    fun setActiveLayerChangesActiveLayerObservableTest(){
        val l=ImageLayers.REFERENCE
        eVM?:throw Exception("evm null")
        eVM?.setActiveLayer(l)
        val v=eVM?.activeLayer?.getOrAwaitValue()?:throw Exception("1")
        Assert.assertEquals(v,l)
    }
    @Test
    fun setActiveLayerChangesParametersTest(){
        eVM?:throw Exception("evm null")
        eVM?.setActiveLayer(ImageLayers.REFERENCE)?:throw Exception("1")
        val v1=eVM?.activeLayer?.getOrAwaitValue()?:throw Exception("2")
        eVM?.setActiveLayer(ImageLayers.SECONDARY)
        val v2=eVM?.activeLayer?.getOrAwaitValue()?:throw Exception("3")
        Assert.assertNotEquals(v1,v2)
    }

    @Test
    fun setReferenceModeTest(){
        eVM?:throw Exception("evm null")
        eVM?.setReferenceMode(ReferenceModes.SECONDARY)
        var v=eVM?.referenceMode?.value?:throw Exception("1")
        Assert.assertEquals(v,ReferenceModes.SECONDARY)
        eVM?.setReferenceMode(ReferenceModes.PRIMARY)
        v= eVM?.referenceMode?.value?:throw Exception("2")
        Assert.assertEquals(v,ReferenceModes.PRIMARY)
    }

    @Test
    fun saveImageToGalleryTest() {
        val format=Bitmap.CompressFormat.JPEG
        eVM ?: throw java.lang.Exception("null evm")
        eVM?.requestImageSave(format) ?: throw Exception("1")
        //verify{imgRepo.sveImageToGalleryWasCalled() }
        coVerify { imgRepo?.saveImageToGallery(any(),any(),format)  }
    }

}