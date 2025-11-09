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

    macosArm64("macosBridge") {
        binaries {
            sharedLib {
                baseName = "macos_bridge"
            }
        }
        compilations.getByName("main") {
            cinterops {
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

// TODO Call this task automatically (on which tasks ?)
tasks.register<Copy>("buildAndCopyMacosBridge") {
    dependsOn("linkReleaseSharedMacosBridge")

    from(layout.buildDirectory.file("bin/macosBridge/releaseShared/libmacos_bridge.dylib"))
    into(rootProject.layout.projectDirectory.dir("composeApp/resources/macos-arm64/libs"))
}
