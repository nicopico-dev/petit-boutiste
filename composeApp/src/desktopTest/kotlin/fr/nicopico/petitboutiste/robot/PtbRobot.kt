/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.robot

import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.nicopico.petitboutiste.PTBViewModel
import fr.nicopico.petitboutiste.PetitBoutisteApp
import fr.nicopico.petitboutiste.fakes.FakeAppStateRepository
import fr.nicopico.petitboutiste.fakes.FakeTemplateManager
import fr.nicopico.petitboutiste.state.Reducer
import fr.nicopico.petitboutiste.ui.theme.invoke
import java.io.File
import javax.imageio.ImageIO

class PtbRobot(
    private val rule: ComposeContentTestRule,
    private val screenshotFolder: File = File(System.getProperty("java.io.tmpdir")),
) {

    init {
        require(screenshotFolder.isDirectory) {
            "$screenshotFolder is not a directory"
        }
        rule.setContent {
            val viewModel = PTBViewModel(
                reducer = Reducer(FakeTemplateManager()),
                appStateRepository = FakeAppStateRepository(),
            )
            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            appTheme {
                PetitBoutisteApp(viewModel)
            }
        }
    }

    fun <P : PartRobot> on(
        part: P,
        block: context(ComposeContentTestRule) P.() -> Unit,
    ): PtbRobot {
        context(rule) {
            part.block()
        }
        return this
    }

    fun takeScreenshot(name: String? = null): PtbRobot {
        val imageBitmap = rule.onRoot().captureToImage()

        val fileName = if (name != null) {
            if (name.endsWith(".png")) name else "$name.png"
        } else {
            "screenshot-${System.currentTimeMillis()}.png"
        }

        val file = File(screenshotFolder, fileName)
        ImageIO.write(imageBitmap.toAwtImage(), "png", file)

        println("Saved screenshot to ${file.absolutePath}")

        return this
    }
}
