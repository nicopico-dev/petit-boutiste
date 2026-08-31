/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.ui.components.foundation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.nicopico.petitboutiste.ui.theme.AppTheme
import fr.nicopico.petitboutiste.ui.theme.colors
import fr.nicopico.petitboutiste.ui.theme.typography
import fr.nicopico.petitboutiste.utils.compose.preview.WrapForPreview
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun PBTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: (() -> Unit)? = null,
) {
    val state = observeTextFieldState(value, onValueChange)
    val theme = AppTheme.current

    Column(modifier) {
        TextField(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            outline = if (isError) Outline.Error else Outline.None,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = { onKeyboardAction?.invoke() },
        )

        if (errorText != null) {
            Text(
                errorText,
                modifier = Modifier.padding(top = 4.dp).align(Alignment.End),
                style = theme.typography.small,
                color = theme.colors.errorColor,
            )
        }
    }
}

@Preview
@Composable
private fun PBTextFieldPreview() {
    WrapForPreview(
        modifier = Modifier.padding(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PBTextField(
                value = "Some text",
                onValueChange = {},
            )

            PBTextField(
                value = "Wrong input (no details)",
                onValueChange = {},
                isError = true,
            )

            PBTextField(
                value = "Wrong input",
                onValueChange = {},
                isError = true,
                errorText = "Something went wrong",
            )
        }
    }
}
