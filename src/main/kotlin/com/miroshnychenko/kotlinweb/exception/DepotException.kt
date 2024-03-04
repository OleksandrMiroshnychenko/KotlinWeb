package com.miroshnychenko.kotlinweb.exception

data class DepotException(override val message: String?, val status: String) : Exception(message)