package com.tsehsrah.maxdrx.utilities

import com.tsehsrah.maxdrx.configs.CONSTANTS.MONITOR_ENTRY_CAPACITY

object ProcessMonitor : IProcessMonitor{
    private val l:ArrayList<Int> =ArrayList(MONITOR_ENTRY_CAPACITY)
    override var count:Int=0
    override fun updateTime(v: Int): Int {
        l.add(v)
        if(l.size>=MONITOR_ENTRY_CAPACITY){
            l.removeAt(0)
        }
        var sum=0
        for(i in l){
            sum+=i
        }
        return (sum+v)/(l.size+1)
    }

}