/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ext

import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat


/**
 * Configures the project for a desktop application built with JetBrains Compose Multiplatform.
 *
 * Sets up the necessary Gradle extensions and configurations to compile and package a desktop application
 * targeting Windows, macOS, and Linux. This method initializes the application metadata, resource paths,
 * and platform-specific configurations.
 *
 * Several configuration choices are hardcoded in this function:
 * - **ProGuard**: The release build type uses a ProGuard configuration file named `compose-desktop.pro`
 *   located in the project directory.
 * - **License**: The `LICENSE` file from the root project is included in the distributions.
 * - **JDK**: The application (and the ProGuard library jars) use the Gradle-resolved JetBrains
 *   Runtime toolchain for [jdkMajorVersion] instead of the ambient `JAVA_HOME`, because the
 *   ProGuard task only picks up the JDK runtime classes from the `jmods` directory of `javaHome`.
 * - **JDK Modules**: The `jdk.unsupported` module is included by default.
 * - **App Resources**: Application resources are expected to be in a `resources` directory within
 *   the project.
 * - **App Icons**: The application icons are expected at `icons/app-icon.*` within the project.
 * - **Target Formats**: The application is packaged as `.dmg` (macOS), `.msi` (Windows), and `.deb` (Linux).
 *
 * @param appName The name of the application to be distributed.
 * @param fullPackageName The full package name for the application (used for bundle identifiers and classpaths).
 * @param mainClass The fully qualified class name of the main application entry point. Defaults to
 *                  `${fullPackageName}.MainKt` if not specified.
 * @param vendorName The vendor name for the application. Defaults to "Nicolas PICON" if not specified.
 * @param enableJniLogs If true, enables verbose JNI logging by adding `-Xcheck:jni` JVM arguments.
 */
fun Project.configureDesktopApplication(
    appName: String,
    fullPackageName: String,
    jdkMajorVersion: Int,
    mainClass: String = "$fullPackageName.MainKt",
    vendorName: String = "Nicolas PICON",
    enableJniLogs: Boolean = false,
) {
    pluginManager.withPlugin("org.jetbrains.compose") {
        // Resolve the JetBrains Runtime toolchain (same one used to compile the app).
        // Using it as `javaHome` guarantees that `<javaHome>/jmods` exists, which the
        // ProGuard task needs to emit `-libraryjars` for the JDK runtime classes.
        val jbrHome = extensions.getByType(JavaToolchainService::class.java)
            .launcherFor {
                languageVersion.set(JavaLanguageVersion.of(jdkMajorVersion))
                vendor.set(JvmVendorSpec.JETBRAINS)
            }
            .get()
            .metadata
            .installationPath
            .asFile
            .absolutePath

        extensions.findByType<ComposeExtension>()?.apply {
            // equivalent to `desktop` generated accessor
            configure<DesktopExtension> {
                application {
                    this.mainClass = mainClass

                    javaHome = jbrHome
                    if (enableJniLogs) {
                        jvmArgs.add("-Xcheck:jni") // Print JNI logs to the console (really verbose !)
                    }

                    buildTypes.release.proguard {
                        configurationFiles.from(project.file("compose-desktop.pro"))
                    }

                    nativeDistributions {
                        packageName = appName
                        packageVersion = version.toString()
                        vendor = vendorName

                        licenseFile.set(rootProject.file("LICENSE"))

                        modules("jdk.unsupported")

                        // Per-platform resources
                        appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

                        targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

                        macOS {
                            setDockNameSameAsPackageName
                            iconFile.set(project.file("icons/app-icon.icns"))
                            bundleID = fullPackageName
                        }
                    }
                }
            }
        }
    }
}
