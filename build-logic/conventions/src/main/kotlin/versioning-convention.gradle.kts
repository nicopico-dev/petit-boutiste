/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

val isCi = System.getenv("CI")?.equals("true", ignoreCase = true) == true
val appVersionProp = findProperty("appVersion")?.toString()

version = if (isCi) {
    require(!appVersionProp.isNullOrBlank()) {
        "CI=true: appVersion Gradle property is required (use -PappVersion=x.y.z)"
    }
    appVersionProp
} else {
    appVersionProp
        ?: "255.255.65535" // Default for development
}
