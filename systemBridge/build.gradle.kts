/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm("desktop")

    compilerOptions {
        this.optIn.addAll(
            "kotlin.concurrent.atomics.ExperimentalAtomicApi",
        )
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            implementation(libs.jetbrains.compose.foundation)
        }
    }
}
