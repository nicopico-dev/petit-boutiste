package fr.nicopico.petitboutiste.utils

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import fr.nicopico.petitboutiste.models.HexString

object HexStringSaver : Saver<HexString, String> {
    override fun SaverScope.save(value: HexString): String? {
        return value.hexString
    }

    override fun restore(value: String): HexString? {
        return HexString(value)
    }
}
