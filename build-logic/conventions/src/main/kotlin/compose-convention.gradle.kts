import gradle.kotlin.dsl.accessors._cdbefa3697c16faa678b079861f279d7.composeCompiler

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

// use `./gradlew compileKotlinDesktop --rerun-tasks` to generate the reports
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose-compiler")
    metricsDestination = layout.buildDirectory.dir("compose-compiler")

    stabilityConfigurationFiles = listOf(
        layout.projectDirectory.file("compose-stability.config")
    )
}

// See DesktopApplication.kt for Compose Desktop app configuration
