package fr.nicopico.petitboutiste

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import fr.nicopico.petitboutiste.models.app.AppEvent
import fr.nicopico.petitboutiste.models.app.selectedTab
import fr.nicopico.petitboutiste.models.ui.TabData
import fr.nicopico.petitboutiste.models.ui.getScreenCharacteristics
import fr.nicopico.petitboutiste.repository.AppStateRepository
import fr.nicopico.petitboutiste.repository.LegacyTemplateManager
import fr.nicopico.petitboutiste.repository.TemplateManager
import fr.nicopico.petitboutiste.repository.WindowStateRepository
import fr.nicopico.petitboutiste.ui.PBMenuBar
import fr.nicopico.petitboutiste.ui.PBTitleBar
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.painterResource
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.styling.TitleBarStyle
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

private val windowStateRepository = WindowStateRepository()
private val appStateRepository = AppStateRepository()

private val reducer = Reducer(
    templateManager = TemplateManager(),
    legacyTemplateManager = LegacyTemplateManager(),
)

const val APP_ID = "fr.nicopico.petitboutiste"

fun main() {
    FileKit.init(appId = APP_ID)
    // Ensure File dialogs have as native appearance on macOS
    // https://filekit.mintlify.app/dialogs/file-picker#customizing-the-dialog
    System.setProperty("apple.awt.application.appearance", "system")

    application {
        val screenCharacteristics = getScreenCharacteristics()
        val windowState = rememberSaveable {
            windowStateRepository.restore(screenCharacteristics) ?: WindowState()
        }
        var appState by rememberSaveable {
            mutableStateOf(appStateRepository.restore())
        }
        val currentTab: TabData by remember {
            derivedStateOf { appState.selectedTab }
        }

        fun onEvent(event: AppEvent) {
            appState = reducer(appState, event)
        }

        // TODO Follow system theme changes
        val isDark = currentSystemTheme == SystemTheme.DARK

        IntUiTheme(
            theme = if (isDark) JewelTheme.darkThemeDefinition() else JewelTheme.lightThemeDefinition(),
            styling = ComponentStyling.default()
                .decoratedWindow(
                    titleBarStyle = if (isDark) TitleBarStyle.dark() else TitleBarStyle.light(),
                )
        ) {
            DecoratedWindow(
                title = "Petit Boutiste",
                icon = painterResource("icons/app-icon.png"),
                onCloseRequest = {
                    windowStateRepository.save(windowState, screenCharacteristics)
                    appStateRepository.save(appState)
                    exitApplication()
                },
                state = windowState,
                content = {
                    PBMenuBar(currentTab, onEvent = ::onEvent)
                    PBTitleBar(appState, onEvent = ::onEvent)
                    App(appState, onEvent = ::onEvent)
                }
            )
        }
    }
}
