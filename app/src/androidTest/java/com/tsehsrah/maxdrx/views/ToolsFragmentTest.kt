package com.tsehsrah.maxdrx.views

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tsehsrah.maxdrx.R
import com.tsehsrah.maxdrx.fakes.FakeImageRepo
import com.tsehsrah.maxdrx.fakes.FakeServiceLocator
import com.tsehsrah.maxdrx.repos.IImageRepository
import com.tsehsrah.maxdrx.viewmodels.EditorVM
import com.tsehsrah.maxdrx.viewmodels.SelectorVM
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ToolsFragmentTest{
    @get:Rule
    var activityScenarioRule = activityScenarioRule<EditorActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val sL= spyk(FakeServiceLocator())

    private val imgRepo: IImageRepository = spyk(FakeImageRepo())

    @BindValue
    val evm= spyk(EditorVM(mockk(),sL, imgRepo))

    @BindValue
    val svm= spyk(SelectorVM(mockk(),sL, imgRepo))

    @Test
    fun seekbar1VisibilityTest(){
        Espresso.onView(ViewMatchers.withId(R.id.tool_seekbar1))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun seekbar1SwipeTest(){
        Espresso.onView(ViewMatchers.withId(R.id.tool_seekbar1))
            .perform(ViewActions.swipeRight())
        verify{evm.updateToolStatus(any())}
    }


    @Test
    fun seekbar2VisibilityTest(){
        Espresso.onView(ViewMatchers.withId(R.id.tool_seekbar2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun seekbar2SwipeTest(){
        Espresso.onView(ViewMatchers.withId(R.id.tool_seekbar2))
            .perform(ViewActions.swipeLeft())
        verify{evm.updateToolStatus(any())}
    }



    @Test
    fun recyclerViewVisibilityTest(){
        Espresso.onView(ViewMatchers.withId(R.id.rcyclr_tool_slct))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun recyclerViewClickTest(){
        onView(withId(R.id.rcyclr_tool_slct))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )
        verify { evm.updateToolStatus(any())}
    }
    @Test
    fun recyclerViewItemLongClickTest(){
        onView(withId(R.id.rcyclr_tool_slct))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    longClick()
                )
            )
        onView(withId(R.id.more_tool_options_lo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }



    @Test
    fun applyButtonVisibilityTest(){
        Espresso.onView(ViewMatchers.withId(R.id.tools_apply))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun applyButtonClickTest(){
        Espresso.onView(ViewMatchers.withId(R.id.tools_apply))
            .perform(click())
        verify{evm.applyChanges()}
    }




}