package fr.nicopico.petitboutiste.ui.components.template

/**
 * Represents the state of dialogs in the Template Management screen.
 * Using an enum ensures only one dialog can be displayed at a time.
 */
enum class TemplateDialogState {
    None,
    Save,
    Load,
    Clear,
    Export,
    Import
}
