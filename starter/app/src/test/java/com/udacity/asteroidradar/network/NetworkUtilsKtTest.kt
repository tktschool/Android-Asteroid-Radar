package com.udacity.asteroidradar.network

import junit.framework.TestCase
import org.junit.Assert

class NetworkUtilsKtTest : TestCase() {

    fun testParseAsteroidsJsonResult() {}

    fun testGetToDaysFormattedDates() {}

    fun testGetEndWeekFormattedDates() {
        val dayStart = getToDaysFormattedDates()
        val dayEnd = getEndWeekFormattedDates()
        Assert.assertEquals(dayStart, "2021-01-14")
        Assert.assertEquals(dayEnd, "2021-01-16")
    }
}