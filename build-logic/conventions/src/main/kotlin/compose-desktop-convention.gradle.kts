import ext.libs
import org.jetbrains.compose.ComposePlugin


/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

// See DesktopApplication.kt for Compose Desktop app configuration

// use `./gradlew compileKotlinDesktop --rerun-tasks` to generate the reports
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose-compiler")
    metricsDestination = layout.buildDirectory.dir("compose-compiler")

    stabilityConfigurationFiles = listOf(
        layout.projectDirectory.file("compose-stability.config")
    )
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("jetbrains.compose.runtime").get())
            implementation(libs.findLibrary("jetbrains.compose.foundation").get())
            implementation(libs.findLibrary("jetbrains.compose.components.resources").get())
            implementation(libs.findLibrary("jetbrains.compose.ui").get())
            implementation(libs.findLibrary("jetbrains.compose.ui.tooling.preview").get())

            implementation(libs.findLibrary("androidx.lifecycle.viewmodelCompose").get())
            implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())

            implementation(libs.findBundle("jewel").get())
        }

        val desktopMain by getting
        desktopMain.dependencies {
            // Help the compiler to choose the right `compose` extension property
            val compose = extensions.getByType<ComposePlugin.Dependencies>()
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material")
            }
        }
    }
}
