/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.system

private const val TAG = "BRIDGE"

internal fun internalLog(msg: String, error: Throwable? = null) {
    if (error != null) {
        val stacktrace = error.stackTraceToString()
        System.err.println("$TAG - $msg\n$stacktrace")
    } else {
        println("$TAG - $msg")
    }
}
