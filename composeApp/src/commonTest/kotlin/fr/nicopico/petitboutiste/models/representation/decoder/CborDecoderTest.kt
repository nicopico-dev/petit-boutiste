/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.representation.decoder

import fr.nicopico.petitboutiste.models.representation.DataRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class CborDecoderTest {

    @Test
    fun `decode simple CBOR string`() {
        // CBOR for "Hello" is 65 48 65 6c 6c 6f
        val payload = byteArrayOf(0x65.toByte(), 0x48.toByte(), 0x65.toByte(), 0x6c.toByte(), 0x6c.toByte(), 0x6f.toByte())
        val result = DataRenderer.Cbor.decodeCbor(payload)
        assertEquals("\"Hello\"", result)
    }

    @Test
    fun `decode simple CBOR integer`() {
        // CBOR for 42 is 18 2a
        val payload = byteArrayOf(0x18.toByte(), 0x2a.toByte())
        val result = DataRenderer.Cbor.decodeCbor(payload)
        assertEquals("42", result)
    }

    @Test
    fun `decode CBOR map`() {
        // CBOR for {"a": 1, "b": "v"} is a2 61 61 01 61 62 61 76
        val payload = byteArrayOf(
            0xa2.toByte(), // map(2)
            0x61.toByte(), 0x61.toByte(), // text(1) "a"
            0x01.toByte(), // unsigned(1)
            0x61.toByte(), 0x62.toByte(), // text(1) "b"
            0x61.toByte(), 0x76.toByte()  // text(1) "v"
        )
        val result = DataRenderer.Cbor.decodeCbor(payload)
        assertEquals("{\"a\":1,\"b\":\"v\"}", result)
    }

    @Test
    fun `decode CBOR array`() {
        // CBOR for [1, 2, 3] is 83 01 02 03
        val payload = byteArrayOf(0x83.toByte(), 0x01.toByte(), 0x02.toByte(), 0x03.toByte())
        val result = DataRenderer.Cbor.decodeCbor(payload)
        assertEquals("[1,2,3]", result)
    }

    @Test
    fun `decode invalid CBOR throws exception`() {
        val payload = byteArrayOf(0xff.toByte(), 0xff.toByte())
        assertFails {
            DataRenderer.Cbor.decodeCbor(payload)
        }
    }
}
