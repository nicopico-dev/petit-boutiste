package fr.nicopico.petitboutiste

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.ui.AppScreen
import fr.nicopico.petitboutiste.ui.infra.savers.GroupDefinitionsSaver
import fr.nicopico.petitboutiste.ui.infra.savers.HexStringSaver
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var data by rememberSaveable(stateSaver = HexStringSaver) {
        mutableStateOf(HexString(""))
    }
    var groupDefinitions by rememberSaveable(stateSaver = GroupDefinitionsSaver) {
        mutableStateOf(emptyList())
    }

    AppScreen(
        inputData = data,
        groupDefinitions = groupDefinitions,
        onInputDataChanged = { data = it },
        onGroupDefinitionsChanged = { groupDefinitions = it }
    )
}
