/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import fr.nicopico.petitboutiste.models.representation.DataRenderer

private val cborMapper = ObjectMapper(CBORFactory())
private val jsonMapper = ObjectMapper()

fun DataRenderer.decodeCbor(byteArray: ByteArray): String {
    require(this == DataRenderer.Cbor)
    val node = cborMapper.readTree(byteArray)
    return jsonMapper.writeValueAsString(node)
}
