package com.tsehsrah.maxdrx.views

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ImageSelectFragmentTest{
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

    fun addImage(){
        svm.addNewWorkFiles(mockk())
        Espresso.onView(ViewMatchers.withId(R.id.Img_slct_rclr))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun addImageAndSelectionTest(){
        addImage()
        Espresso.onView(ViewMatchers.withId(R.id.Img_slct_rclr))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        verify { svm.setCurrentPos(any()) }

    }
    @Test
    fun addImageLongClickTest(): Unit = runBlocking(){
        addImage()
        Espresso.onView(ViewMatchers.withId(R.id.Img_slct_rclr))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.longClick()
                )
            )
        delay(500)
        Espresso.onView(ViewMatchers.withId(R.id.image_select_popup_lo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


}