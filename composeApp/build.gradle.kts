import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(compose.materialIconsExtended)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.settings)

            implementation("org.jetbrains.compose.material3.adaptive:adaptive:1.1.2")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-layout:1.1.2")
            implementation("org.jetbrains.compose.material3.adaptive:adaptive-navigation:1.1.2")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
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
            packageVersion = "1.0.0"
        }
    }
}

// Compose Hot-Reload! (https://github.com/JetBrains/compose-hot-reload)
composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}
tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set("fr.nicopico.petitboutiste.MainKt")
}
