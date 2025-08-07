package fr.nicopico.petitboutiste.ui.infra.savers

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import fr.nicopico.petitboutiste.models.DataString
import fr.nicopico.petitboutiste.models.HexString

object HexStringSaver : Saver<DataString, String> {
    override fun SaverScope.save(value: DataString): String? {
        return value.hexString
    }

    override fun restore(value: String): DataString? {
        return HexString(value)
    }
}
