/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    compilerOptions {
        this.optIn.addAll(
            "kotlinx.cinterop.BetaInteropApi",
            "kotlinx.cinterop.ExperimentalForeignApi",
            "kotlin.experimental.ExperimentalNativeApi",
        )
    }

    macosArm64 {
        binaries {
            sharedLib {
                baseName = "native_bridge"
            }
        }
        compilations.getByName("main") {
            cinterops {
                @Suppress("unused")
                val jni by creating {
                    packageName = "fr.nicopico.petitboutiste.system.bridge.jni"
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

//region buildAndCopyNativeBridges
tasks.register("buildAndCopyNativeBridges") {
    dependsOn("buildAndCopyMacosNativeBridge")
}

tasks.register<Copy>("buildAndCopyMacosNativeBridge") {
    dependsOn("linkReleaseSharedMacosArm64")

    from(layout.buildDirectory.file("bin/macosArm64/releaseShared/libnative_bridge.dylib"))
    into(rootProject.layout.projectDirectory.dir("composeApp/resources/macos-arm64/libs"))
}
//endregion
