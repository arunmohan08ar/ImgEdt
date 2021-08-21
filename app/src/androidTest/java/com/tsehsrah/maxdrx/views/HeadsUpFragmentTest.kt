package com.tsehsrah.maxdrx.views

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.ImageLayers
import com.tsehsrah.maxdrx.fakes.FakeImageRepo
import com.tsehsrah.maxdrx.fakes.FakeServiceLocator
import com.tsehsrah.maxdrx.launchFragmentInHiltContainer
import com.tsehsrah.maxdrx.viewmodels.EditorVM
import com.tsehsrah.maxdrx.viewmodels.SelectorVM
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class HeadsUpFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val sL=FakeServiceLocator()

    @BindValue
    val evm=spyk(EditorVM(mockk(),sL,FakeImageRepo()))

    @BindValue
    val svm=spyk(SelectorVM(mockk(),sL,FakeImageRepo()))


    @Before
    fun setUp(){
        hiltRule.inject()
        launchFragmentInHiltContainer<HeadsUpFragment>() { }
    }


    @Test
    fun layoutVisibilityTest() {
        onView(withId(R.id.heads_up_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun rightAngleVisibilityTest() {
        onView(withId(R.id.heads_up_right_rotation)).check(matches(isDisplayed()))
    }

    @Test
    fun rotateVisibilityTest() {
        onView(withId(R.id.heads_up_rotate)).check(matches(isDisplayed()))
    }
    @Test
    fun rotationClickTest() {
        onView(withId(R.id.heads_up_rotate)).perform(click())
        onView(withId(R.id.popup_fine_sensitivity_lo)).check(matches(isDisplayed()))
    }
    @Test
    fun rotationLongClickTest() {
        onView(withId(R.id.heads_up_rotate)).perform(longClick())
        verify { evm.setGestureStatus(any()) }
    }


    @Test
    fun zoomVisibilityTest() {
        onView(withId(R.id.heads_up_zoom)).check(matches(isDisplayed()))
    }

    @Test
    fun zoomClickTest() {
        onView(withId(R.id.heads_up_zoom)).perform(click())
        onView(withId(R.id.popup_fine_sensitivity_lo)).check(matches(isDisplayed()))
    }
    @Test
    fun zoomLongClickTest() {
        onView(withId(R.id.heads_up_zoom)).perform(longClick())
        verify { evm.setGestureStatus(any()) }
    }

    @Test
    fun panVisibilityTest() {
        onView(withId(R.id.heads_up_pan)).check(matches(isDisplayed()))
    }
    @Test
    fun panClickTest() {
        onView(withId(R.id.heads_up_pan)).perform(click())
        onView(withId(R.id.popup_fine_sensitivity_lo)).check(matches(isDisplayed()))
    }
    @Test
    fun panLongClickTest() {
        onView(withId(R.id.heads_up_pan)).perform(longClick())
        verify { evm.setGestureStatus(any()) }
    }

    @Test
    fun referenceVisibilityTest() {
        onView(withId(R.id.heads_up_reference)).check(matches(isDisplayed()))
    }
    @Test
    fun referenceClickTest() {
        onView(withId(R.id.heads_up_reference)).perform(click())
        verify { evm.setActiveLayer(ImageLayers.REFERENCE) }
    }
    @Test
    fun referenceLongClickTest(): Unit = runBlocking{
        onView(withId(R.id.heads_up_reference)).perform(longClick())
        delay(200)
        onView(withId(R.id.popup_reference_modes_lo)).check(matches(isDisplayed()))
    }

    @Test
    fun secondaryVisibilityTest() {
        onView(withId(R.id.heads_up_secondary)).check(matches(isDisplayed()))
    }
    @Test
    fun secondaryClickTest() {
        onView(withId(R.id.heads_up_secondary)).perform(click())
        verify { evm.setActiveLayer(ImageLayers.SECONDARY) }
    }

    @Test
    fun previewVisibilityTest() {
        onView(withId(R.id.heads_up_preview)).check(matches(isDisplayed()))
    }
    @Test
    fun previewClickTest() {
        onView(withId(R.id.heads_up_preview)).perform(click())
        verify { evm.setActiveLayer(ImageLayers.PREVIEW) }
    }



}