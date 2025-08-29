import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.kotlinSerialization)
}

version = "2.0.0"

kotlin {
    jvm("desktop")

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.bundles.jewel)
            // TODO Remove Material dependencies
            implementation(compose.material3)
            implementation(compose.materialIconsExtended) // ?
            implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.1.2")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.2")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.2")

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.settings)
            implementation(libs.filekit.dialogs)

            implementation(libs.protobuf.java)
            implementation(libs.protobuf.java.util)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs) {
                // TODO Remove Material dependencies
                // exclude(group = "org.jetbrains.compose.material")
            }
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "fr.nicopico.petitboutiste.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Petit Boutiste"
            packageVersion = version.toString()
            vendor = "Nicolas PICON"

            // TODO Add license
            // licenseFile = rootProject.file("LICENSE")

            macOS {
                setDockNameSameAsPackageName
                iconFile.set(project.file("src/desktopMain/resources/icons/app-icon.icns"))
                bundleID = "fr.nicopico.petitboutiste"
            }
        }
    }
}

// Compose Hot-Reload! (https://github.com/JetBrains/compose-hot-reload)
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}
