/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import ext.configureDesktopApplication

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("compose-convention")

    alias(libs.plugins.kotlinSerialization)
    id("detekt-convention")
    id("kover-convention")
    id("licensee-convention")
    id("versioning-convention")
}

configureDesktopApplication(
    appName = "Petit Boutiste",
    fullPackageName = "fr.nicopico.petitboutiste",
)

kotlin {
    jvm("desktop")

    compilerOptions {
        // NOTE: Pre-release options must be mirrored in the embedded Kotlin compiler to prevent the error
        // "Class 'fr.nicopico.petitboutiste.scripting.PetitBoutisteApi' was compiled by a pre-release version of Kotlin and cannot be loaded by this version of the compiler"
        // (see `ScriptHost` class)
        freeCompilerArgs.add("-Xwhen-guards")
        freeCompilerArgs.add("-Xexplicit-backing-fields")
        optIn.add("kotlin.concurrent.atomics.ExperimentalAtomicApi")
    }

    jvmToolchain {
        // Runs with JBR-21 for Jewel L&F
        languageVersion = JavaLanguageVersion.of(21)
        @Suppress("UnstableApiUsage")
        vendor = JvmVendorSpec.JETBRAINS
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.ui.tooling.preview)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.bundles.jewel)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.settings)
            implementation(libs.filekit.dialogs)

            implementation(libs.protobuf.java)
            implementation(libs.protobuf.java.util)

            // kotlin("something") -> "org.jetbrains.kotlin:kotlin-something
            implementation(kotlin("scripting-jvm"))
            implementation(kotlin("scripting-jvm-host"))
            implementation(kotlin("scripting-common"))
            implementation(kotlin("script-runtime"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material")
            }
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

// Ensure macOS native bridge is built and copied before building composeApp
// This makes :composeApp:build depend on :macosBridge:buildAndCopyMacosBridge
// so the libmacos_bridge.dylib is available under composeApp/resources before packaging.
tasks
    // We cannot use `tasks.named("prepareAppResources")`
    // because this task is created lazily
    .matching { it.name == "prepareAppResources" }
    .configureEach {
        dependsOn(":macosBridge:buildAndCopyMacosBridge")
    }
