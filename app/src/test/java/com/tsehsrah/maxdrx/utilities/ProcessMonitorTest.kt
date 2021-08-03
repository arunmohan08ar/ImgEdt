package com.tsehsrah.maxdrx.utilities

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test


class ProcessMonitorTest{
    @After
    fun tearDown(){
        ProcessMonitor.reset()
    }
    @Test
    fun  `verify function`(){
        ProcessMonitor.updateTime(5)
        ProcessMonitor.updateTime(5)
        val v=ProcessMonitor.updateTime(6)
        assertThat(v).isEqualTo((22/4))
    }
}