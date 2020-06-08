package com.dkarakaya.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DateTimeFormatterTest {

    @Test
    fun `GIVEN date as string WHEN dateFormatter used THEN format date with time`() {
        val input = dateFormatter("2020-12-12 12:00:00.543567")

        val expectedOutput = "12:09 12.12.2020"

        assertEquals(expectedOutput, input)
    }

    @Test
    fun `GIVEN date as string WHEN shortDateFormatter used THEN format date without time`() {
        val input = shortDateFormatter("2020-12-12 12:00:00.543567")

        val expectedOutput = "12.12"

        assertEquals(expectedOutput, input)
    }
}
