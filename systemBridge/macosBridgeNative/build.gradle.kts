/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

// TODO rename module to handle different native bridge (Windows, Linux, etc.)
kotlin {
    compilerOptions {
        this.optIn.addAll(
            "kotlinx.cinterop.BetaInteropApi",
            "kotlinx.cinterop.ExperimentalForeignApi",
            "kotlin.experimental.ExperimentalNativeApi",
        )
    }

    macosArm64("macosBridge") {
        binaries {
            sharedLib {
                baseName = "macos_bridge"
            }
        }
        compilations.getByName("main") {
            cinterops {
                @Suppress("unused")
                val jni by creating {
                    packageName = "fr.nicopico.macos.jni"
                    val javaHome = File(System.getProperty("java.home"))
                    includeDirs(
                        Callable { javaHome.resolve("include/") },
                        Callable { javaHome.resolve("include/darwin") },
                    )
                }
            }
        }
    }
}

tasks.register<Copy>("buildAndCopyMacosBridge") {
    dependsOn("linkReleaseSharedMacosBridge")

    from(layout.buildDirectory.file("bin/macosBridge/releaseShared/libmacos_bridge.dylib"))
    into(rootProject.layout.projectDirectory.dir("composeApp/resources/macos-arm64/libs"))
}
