package com.tsehsrah.maxdrx.views

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.configs.CONSTANTS
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
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ImagePreviewFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val sL= FakeServiceLocator()

    @BindValue
    val evm= spyk(EditorVM(mockk(),sL, FakeImageRepo()))


    @Before
    fun setUp(){
        hiltRule.inject()
        launchFragmentInHiltContainer<ImagePreviewFragment>() { }
    }


    @Test
    fun layoutVisibilityTest() {
        Espresso.onView(ViewMatchers.withId(R.id.img_Preview_Fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun headsUpTextVisibilityTest() {
        Espresso.onView(ViewMatchers.withId(R.id.user_heads_up))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun progressVisibilityTest() {
        Espresso.onView(ViewMatchers.withId(R.id.proc_prgrsBr))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }
    @Test
    fun imageVisibilityTest() {
        Espresso.onView(ViewMatchers.withId(R.id.imgvw_prvw))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun imageQuadTapTest(): Unit = runBlocking{
        Espresso.onView(ViewMatchers.withId(R.id.imgvw_prvw)).perform(ViewActions.doubleClick())
        delay(CONSTANTS.QUAD_TAP_DELAY)
        Espresso.onView(ViewMatchers.withId(R.id.imgvw_prvw)).perform(ViewActions.doubleClick())
        verify{evm.updateImageParameters(any())}
    }

    @Test
    fun imageSwipeTest(): Unit = runBlocking{
        Espresso.onView(ViewMatchers.withId(R.id.imgvw_prvw)).perform(ViewActions.swipeDown())
        verify{evm.updateImageParameters(any())}
    }


}