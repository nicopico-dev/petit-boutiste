package fr.nicopico.petitboutiste.ui.theme.system

private const val AWT_APP_APPEARANCE = "apple.awt.application.appearance"

fun followSystemTheme() {
    System.setProperty(AWT_APP_APPEARANCE, "system")
}
