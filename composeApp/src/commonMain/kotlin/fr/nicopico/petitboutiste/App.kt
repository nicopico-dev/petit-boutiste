package fr.nicopico.petitboutiste

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import fr.nicopico.petitboutiste.models.HexString
import fr.nicopico.petitboutiste.ui.AppContent
import fr.nicopico.petitboutiste.utils.HexStringSaver
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var data by rememberSaveable(stateSaver = HexStringSaver) {
        mutableStateOf(HexString(""))
    }
    AppContent(data) {
        data = it
    }
}
