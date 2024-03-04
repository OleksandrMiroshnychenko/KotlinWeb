package com.miroshnychenko.kotlinweb.filter.wrapper

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream

class CachedBodyServletInputStream(streamCopy: String) : ServletInputStream() {
    private val cachedBodyInputStream: InputStream = ByteArrayInputStream(streamCopy.toByteArray())

    override fun isFinished(): Boolean {
        try {
            return cachedBodyInputStream.available() == 0
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun setReadListener(readListener: ReadListener) {
    }


    @Throws(IOException::class)
    override fun read(): Int {
        return cachedBodyInputStream.read()
    }
}