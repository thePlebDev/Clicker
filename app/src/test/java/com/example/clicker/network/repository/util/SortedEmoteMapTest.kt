package com.example.clicker.network.repository.util

import com.example.clicker.util.SortedEmoteMap
import org.junit.Test
import org.junit.Assert

class SortedEmoteMapTest {

    @Test
    fun hash_code_test(){
        /**GIVEN*/
        val givenString ="testing"
        val secondGivenString = "testing"
        val sortedEmoteMap = SortedEmoteMap()

        /**WHEN*/
        val hashCodeOne = sortedEmoteMap.hashCodeCyclicShift(givenString)
        val hashCodeTwo = sortedEmoteMap.hashCodeCyclicShift(secondGivenString)


        /**THEN*/
        Assert.assertEquals(hashCodeOne,hashCodeTwo)

    }

    @Test
    fun hash_normal_code_test(){
        /**GIVEN*/
        val givenString ="testing"
        val secondGivenString = "testing"

        /**WHEN*/
        val hashCodeOne = givenString.hashCode()
        val hashCodeTwo = secondGivenString.hashCode()


        /**THEN*/
        Assert.assertEquals(hashCodeOne,hashCodeTwo)

    }
}