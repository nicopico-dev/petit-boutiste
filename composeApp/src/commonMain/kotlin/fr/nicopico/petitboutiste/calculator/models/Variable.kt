/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.calculator.models

data class Variable(
    val payload: Payload,
    val property: Property,
) {
    @JvmInline
    value class Payload(val name: String) {
        override fun toString(): String = name
    }

    enum class Property(val code: String) {
        START("start"),
        END("end"),
        VALUE("value"),
        NONE("")
        ;

        override fun toString(): String = code
    }

    companion object {
        val LAST = Variable(Payload("LAST"), Property.NONE)
    }
}
