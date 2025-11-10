import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.kotlinSerialization)
}

//region Version management
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
//endregion

kotlin {
    jvm("desktop")

    compilerOptions {
        freeCompilerArgs.add("-Xwhen-guards")
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.bundles.jewel)

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
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material")
            }
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "fr.nicopico.petitboutiste.MainKt"

        // JAVA_HOME must point to a JBR-21 or more recent
        // ex: ~/Library/Java/JavaVirtualMachines/jbr-21.0.6/Contents/Home
        javaHome = System.getenv("JAVA_HOME")
        jvmArgs.add("-Xcheck:jni") // Add debug information to JNI calls

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }

        nativeDistributions {
            packageName = "Petit Boutiste"
            packageVersion = version.toString()
            vendor = "Nicolas PICON"

            // TODO Add license
            // licenseFile = rootProject.file("LICENSE")

            modules("jdk.unsupported")

            // Per-platform resources
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            macOS {
                setDockNameSameAsPackageName
                iconFile.set(project.file("icons/app-icon.icns"))
                bundleID = "fr.nicopico.petitboutiste"
            }
        }
    }
}
