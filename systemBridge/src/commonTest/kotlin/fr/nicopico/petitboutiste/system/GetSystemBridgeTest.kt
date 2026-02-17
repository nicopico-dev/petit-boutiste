/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.system

import fr.nicopico.petitboutiste.system.bridge.DefaultBridge
import fr.nicopico.petitboutiste.system.bridge.MacosBridge
import kotlin.test.Test
import kotlin.test.assertTrue

class GetSystemBridgeTest {

    @Test
    fun `getSystemBridge should return a MacosBridge on macOS`() {
        // GIVEN
        val getSystemProperty = MockGetSystemProperty(osName = "Mac OS X")

        // WHEN
        val bridge = getSystemBridge(getSystemProperty)

        // THEN
        assertTrue(bridge is MacosBridge)
    }

    @Test
    fun `getSystemBridge should return a DefaultBridge on other systems`() {
        // GIVEN
        val getSystemProperty = MockGetSystemProperty(osName = "Windows")

        // WHEN
        val bridge = getSystemBridge(getSystemProperty)

        // THEN
        assertTrue(bridge is DefaultBridge)
    }

    @Test
    fun `getSystemBridge should return a DefaultBridge if unable to get the OS name`() {
        // GIVEN
        val getSystemProperty = MockGetSystemProperty(osName = null)

        // WHEN
        val bridge = getSystemBridge(getSystemProperty)

        // THEN
        assertTrue(bridge is DefaultBridge)
    }

    private class MockGetSystemProperty(val osName: String?) : Function1<String, String?> {
        override fun invoke(p1: String): String? {
            return when(p1) {
                "os.name" -> osName
                else -> null
            }
        }
    }
}
