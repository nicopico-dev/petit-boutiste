package fr.nicopico.petitboutiste

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val TIMESTAMP_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

private fun timestamp(): String = LocalDateTime.now().format(TIMESTAMP_FORMATTER)

fun log(msg: String) {
    println("[${timestamp()}] $msg")
}

fun logError(msg: String) {
    System.err.println("[${timestamp()}] $msg")
}
