/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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

    // TODO Enable WASM target
//    wasmJs {
//        browser()
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("jetbrains.compose.runtime").get())
            implementation(libs.findLibrary("jetbrains.compose.foundation").get())
            implementation(libs.findLibrary("jetbrains.compose.components.resources").get())
            implementation(libs.findLibrary("jetbrains.compose.ui").get())
            implementation(libs.findLibrary("jetbrains.compose.ui.tooling.preview").get())

            implementation(libs.findLibrary("androidx.lifecycle.viewmodelCompose").get())
            implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
        }

        val desktopMain by getting
        desktopMain.dependencies {
            // Help the compiler to choose the right `compose` extension property
            val compose = extensions.getByType<ComposePlugin.Dependencies>()
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material")
            }

            // Cannot use the `jewel` bundle as we need to exclude some transitive dependencies
            // (see `dependencies` block below)
            //implementation(libs.findBundle("jewel").get())
        }

        val desktopTest by getting
        desktopTest.dependencies {
            // Help the compiler to choose the right `compose` extension property
            val compose = extensions.getByType<ComposePlugin.Dependencies>()
            implementation(compose.desktop.currentOs)
            implementation(libs.findLibrary("jetbrains.compose.ui.test.junit4").get())
        }

        // TODO Enable WASM target
        //val wasmJsMain by getting

    }
}

dependencies {
    // Mirrors the "jewel" bundle from the version catalog (which cannot be declared in the
    // source set DSL because bundles do not support dependency configuration actions).
    //
    // The intellij-platform-icons modules transitively pull the IntelliJ fork of
    // kotlinx-coroutines (org.jetbrains.intellij.deps.kotlinx:kotlinx-coroutines-core),
    // which duplicates the stock coroutines classes in the packaged application and breaks
    // proguardReleaseJars ("unresolved references to program class members"). The stock
    // coroutines used by the app is API-compatible for these modules, so the fork is excluded.
    listOf(
        libs.findLibrary("jewel-int-ui-standalone").get(),
        libs.findLibrary("jewel-int-ui-decorated-window").get(),
        libs.findLibrary("intellij-platform-icons").get(),
    ).forEach { dependency ->
        "desktopMainImplementation"(dependency) {
            exclude(group = "org.jetbrains.intellij.deps.kotlinx")
        }
    }
}
