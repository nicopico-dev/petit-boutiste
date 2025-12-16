#!/bin/sh
#
# This Source Code Form is subject to the terms of the Mozilla Public
#  License, v. 2.0. If a copy of the MPL was not distributed with this
#  file, You can obtain one at https://mozilla.org/MPL/2.0/.
#

act -P macos-latest=-self-hosted -W .github/workflows/macos-release.yml -e .github/act/act_release_event.json --artifact-server-path /tmp/act
