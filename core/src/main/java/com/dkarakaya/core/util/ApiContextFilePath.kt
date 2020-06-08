package com.dkarakaya.core.util

class ApiContextFilePath {

    operator fun invoke(): String? {
        return filePath
    }

    companion object {
        var filePath: String? = null
    }
}
