/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fr.nicopico.petitboutiste.models.definition

import fr.nicopico.petitboutiste.models.data.DataString
import fr.nicopico.petitboutiste.models.representation.Representation

fun createFullPayloadByteGroup(
    dataString: DataString,
    representation: Representation,
): ByteGroup {
    return ByteGroup(
        bytes = dataString.hexStringValue.windowed(2, 2),
        startIndex = 0,
        definition = ByteGroupDefinition.createFromRange(
            indexes = 0..<(dataString.hexStringValue.length / 2),
            representation = representation,
        )
    )
}
