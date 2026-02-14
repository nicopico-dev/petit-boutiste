/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ext

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider

internal val Project.libs: VersionCatalog
    get() = extensions
        .getByType(VersionCatalogsExtension::class.java)
        .named("libs")

internal fun VersionCatalog.libraries(alias: String): Provider<MinimalExternalModuleDependency?> {
    return findLibrary(alias).get()
}

internal fun VersionCatalog.bundles(alias: String): Provider<ExternalModuleDependencyBundle?> {
    return findBundle(alias).get()
}

internal fun VersionCatalog.versions(alias: String): String {
    val versionConstraint = findVersion(alias).get()
    return versionConstraint.toString()
}
