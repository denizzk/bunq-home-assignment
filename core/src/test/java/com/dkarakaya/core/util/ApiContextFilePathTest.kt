package com.dkarakaya.core.util

import org.junit.Assert.*
import org.junit.Test

class ApiContextFilePathTest {

    @Test
    fun `GIVEN file path to ApiContextFilePath WHEN ApiContextFilePath invoked THEN return the file path`() {
        ApiContextFilePath.filePath = "dummy/dummy/dummy.txt"

        val expectedOutput = "dummy/dummy/dummy.txt"

        assertEquals(expectedOutput, ApiContextFilePath().invoke())
    }
}
