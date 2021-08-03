package com.tsehsrah.maxdrx.utilities

import com.google.common.truth.Truth.assertThat
import com.tsehsrah.maxdrx.configs.CONSTANTS.MAX_SCALE
import com.tsehsrah.maxdrx.utilities.ImageUtilities.angleBetweenLines
import com.tsehsrah.maxdrx.utilities.ImageUtilities.calculateDiffXYtoAngle
import com.tsehsrah.maxdrx.utilities.ImageUtilities.getImageRatio
import org.junit.Assert
import org.junit.Test

class ImageUtilitiesTest{

    //Image ratio
    @Test
    fun `all ones returns one`(){
        val v=getImageRatio(1,1,1,1f)
        assertThat(v).isEqualTo(1f)
    }

    @Test
    fun `verify for random values`(){
        val v=getImageRatio(100,200,50,1f)
        assertThat(v).isEqualTo(.25f)
    }


    // diff XY as angle
    @Test
    fun `diffXY zero and 360 angle makes zero difference to coordinates`(){
        var p:Pair<Int,Int> = calculateDiffXYtoAngle(1,1,0.0,1f)
        assert_pair_contains_values_as(p,1,1)
        p = calculateDiffXYtoAngle(1,1,360.0,1f)
        assert_pair_contains_values_as(p,1,1)
    }
    @Test
    fun `diffXY verify 90 degree values`(){
        val p:Pair<Int,Int> = calculateDiffXYtoAngle(1,1,90.0,1f)
        assert_pair_contains_values_as(p,1,-1)
    }
    @Test
    fun `diffXY verify large scale value`(){
        val x=100
        val y=100
        val p:Pair<Int,Int> = calculateDiffXYtoAngle(x,y,180.0,100f)
        assert_pair_contains_values_as(p,(-x/MAX_SCALE).toInt(),(-y/MAX_SCALE.toInt()))
    }


    //angle between lines
    @Test
    fun `same line returns zero`(){
        val v= angleBetweenLines(1,0f, 0f, 5f, 5f, 0f, 0f, 5f, 5f)
        assertThat(v).isZero()
    }
    @Test
    fun `90 degree test`(){
        val v= angleBetweenLines(1,0f, 0f, 5f, 5f, 5f, 0f, 0f, 5f)
        Assert.assertEquals(90f,v,.001f)
    }
    @Test
    fun `180 degree test with ratio`(){
        val v= angleBetweenLines(2,0f, 0f, 5f, 5f, 5f, 5f, 0f, 0f)
        assertThat(v).isEqualTo(90f)
    }




    private fun <Tf,Ts> assert_pair_contains_values_as( pair: Pair<Tf,Ts>,f:Tf, s:Ts){
        assertThat(pair.first).isEqualTo(f)
        assertThat(pair.second).isEqualTo(s)
    }
}