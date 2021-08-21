package com.tsehsrah.maxdrx.views

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
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
import io.mockk.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
internal class EditorActivityTest{
    @get:Rule
    var activityScenarioRule = activityScenarioRule<EditorActivity>()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val sL=spyk(FakeServiceLocator())

    private val imgRepo:IImageRepository=spyk(FakeImageRepo())

    @BindValue
    val evm= spyk(EditorVM(mockk(),sL, imgRepo))

    @BindValue
    val svm= spyk(SelectorVM(mockk(),sL, imgRepo))

    @Test
    fun activityLayoutVisibilityTest(){
        onView(withId(R.id.editor_activity_layout)).check(matches(isDisplayed()))
    }
    @Test
    fun repositoryBusyIndicatorVisibilityTest(){
        onView(withId(R.id.repo_progress)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun previewFragmentVisibilityTest(){
        onView(withId(R.id.img_Preview_Fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun headsUpFragmentVisibilityTest(){
        onView(withId(R.id.heads_up_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun imageSelectFragmentVisibilityTest(){
        onView(withId(R.id.image_select_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun toolsFragmentVisibilityTest(){
        onView(withId(R.id.tools_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun recyclerViewVisibilityTest() {
        onView(withId(R.id.tools_up)).perform(click())
        onView(withId(R.id.Img_slct_rclr))
            .check(matches(isEnabled()))
    }

    @Test
    fun addImageOptionTest() {
        onView(withId(R.id.Act_edt_Opt_Opn)).perform(ViewActions.click())
        verify { svm.setExpectNewFile(any()) }
    }

    @Test
    fun addImageCallsRepoTest(){
        svm.addNewWorkFiles(mockk())
        verify { imgRepo.addNewWrkFiles(any(),any(),any()) }
    }

}