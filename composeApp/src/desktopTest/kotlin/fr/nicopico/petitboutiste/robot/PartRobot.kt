/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.robot

sealed interface PartRobot {

    /**
     * Make [ScreenshotHost.takeScreenshot] available to all [PartRobot] without an explicit dependency
     * as long as a [ScreenshotHost] is available in the context
     */
    context(screenshotHost: ScreenshotHost)
    fun takeScreenshot(name: String? = null) {
        screenshotHost.takeScreenshot(name = name)
    }
}
