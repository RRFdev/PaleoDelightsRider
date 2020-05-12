package com.rrdsolutions.paleodelightsrider

import org.junit.Test

import org.junit.Assert.*
import java.lang.StrictMath.abs

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun division(){
        assertEquals(10, (60-40)/2)
    }
    @Test fun difference(){
        assertEquals(2, abs(2-4))
    }
}