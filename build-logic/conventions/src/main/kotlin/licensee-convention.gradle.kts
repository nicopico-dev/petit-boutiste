import app.cash.licensee.SpdxId
import gradle.kotlin.dsl.accessors._b16340da17b4ee7b5aa51ae8b04976cd.licensee

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    id("app.cash.licensee")
}

licensee {
    with(SpdxId) {
        allow(Apache_20)
        allow(MIT)
        allow(BSD_3_Clause)

        allowUrl("https://github.com/hypfvieh/dbus-java/blob/master/LICENSE") {
            because("MIT (self-hosted)")
        }

        allowUrl("https://github.com/vinceglb/FileKit/blob/main/LICENSE") {
            because("MIT (self-hosted)")
        }

        ignoreDependencies(
            groupId = "com.jetbrains.intellij.platform",
            artifactId = "icons",
            options = {
                because("Apache 2.0 (see https://github.com/JetBrains/intellij-community)")
            }
        )
    }
}
