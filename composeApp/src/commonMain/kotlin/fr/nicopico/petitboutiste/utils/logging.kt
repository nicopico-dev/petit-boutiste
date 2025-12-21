/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.utils

import fr.nicopico.macos.MacosBridge
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val TIMESTAMP_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

private fun timestamp(): String = LocalDateTime.now().format(TIMESTAMP_FORMATTER)

fun log(msg: String) {
    println("[${timestamp()}] $msg")
    MacosBridge.log(msg)
}

fun logError(msg: String) {
    System.err.println("[${timestamp()}] $msg")
    MacosBridge.log("ERROR: $msg")
}
