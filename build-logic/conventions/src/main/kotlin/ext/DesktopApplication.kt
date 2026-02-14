/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ext

import gradle.kotlin.dsl.accessors._cdbefa3697c16faa678b079861f279d7.desktop
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.compose.ComposeExtension
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
    mainClass: String = "$fullPackageName.MainKt",
    vendorName: String = "Nicolas PICON",
    enableJniLogs: Boolean = false,
) {
    pluginManager.withPlugin("org.jetbrains.compose") {
        extensions.findByType<ComposeExtension>()?.apply {
            desktop {
                application {
                    this.mainClass = mainClass

                    // JAVA_HOME must point to a JBR-21 or more recent
                    // ex: ~/Library/Java/JavaVirtualMachines/jbr-21.0.6/Contents/Home
                    javaHome = System.getenv("JAVA_HOME")
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
