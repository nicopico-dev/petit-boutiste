/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

import fr.nicopico.petitboutiste.system.SystemBridge
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val TIMESTAMP_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

private fun timestamp(): String = LocalDateTime.now().format(TIMESTAMP_FORMATTER)

fun log(msg: String) {
    println("[${timestamp()}] $msg")
    SystemBridge.log(msg)
}

fun logError(msg: String, error: Throwable? = null) {
    val stacktrace = error?.stackTraceToString()
        ?.let { "\n$it" }
        .orEmpty()
    System.err.println("[${timestamp()}] $msg$stacktrace")
    SystemBridge.log("ERROR: $msg")
}
