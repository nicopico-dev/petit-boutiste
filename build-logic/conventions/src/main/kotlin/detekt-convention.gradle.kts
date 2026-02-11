import ext.libraries
import ext.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("${project.rootDir}/detekt.yml")
    baseline = File("${project.rootDir}/detekt-baseline-${project.name}.xml")
}

dependencies {
    detektPlugins(libs.libraries("buildlogic-detekt-ruleset-compose"))
}

run {
    val detektSourceDirs = setOf(
        "src/commonMain/kotlin",
        "src/desktopMain/kotlin"
    )
    tasks.withType<Detekt>().configureEach {
        setSource(files(detektSourceDirs))
    }
    tasks.withType<DetektCreateBaselineTask>().configureEach {
        setSource(files(detektSourceDirs))
    }
}
