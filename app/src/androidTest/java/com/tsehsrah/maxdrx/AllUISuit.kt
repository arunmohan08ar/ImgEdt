package com.tsehsrah.maxdrx

import com.tsehsrah.maxdrx.repos.ImageRepoTest
import com.tsehsrah.maxdrx.viewmodels.EditorVMTest
import com.tsehsrah.maxdrx.viewmodels.SelectorVMTest
import com.tsehsrah.maxdrx.views.*
import com.tsehsrah.maxdrx.views.EditorActivityTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ImageRepoTest::class,
    EditorVMTest::class,
    SelectorVMTest::class,
    EditorActivityTest::class,
    HeadsUpFragment::class,
    ImagePreviewFragmentTest::class,
    ImageSelectFragmentTest::class,
    ToolsFragmentTest::class

)
class AllSuit