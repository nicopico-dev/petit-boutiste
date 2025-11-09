package fr.nicopico.petitboutiste.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.ui.theme.invoke
import fr.nicopico.petitboutiste.ui.theme.system.adaptWindowAppearance
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun RenameTabDialog(
    currentName: String?,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val textFieldState = rememberTextFieldState(currentName ?: "")

    val submit = {
        val newTabName = textFieldState.text.toString()
        if (newTabName != currentName && newTabName.isNotBlank()) {
            onSubmit(newTabName)
        }
        onDismiss()
    }

    DialogWindow(
        onCloseRequest = onDismiss,
        title = "Rename",
        state = DialogState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(250.dp, 140.dp),
        ),
        content = {
            adaptWindowAppearance()
            val appTheme = AppTheme.current

            Column(
                Modifier
                    .background(appTheme.colors.windowBackgroundColor)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // DialogWindow creates a new composition root that does not inherit the parent theme,
                // so we need to reapply the theme here to ensure correct text color and styling.
                appTheme {
                    Text("Rename this tab")
                }

                TextField(
                    state = textFieldState,
                    placeholder = { Text("Tab Name") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onKeyboardAction = { submit() }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.End),
                ) {
                    OutlinedButton(
                        content = { Text("Cancel") },
                        onClick = onDismiss,
                    )

                    DefaultButton(
                        content = { Text("Rename") },
                        onClick = submit
                    )
                }
            }
        }
    )
}
