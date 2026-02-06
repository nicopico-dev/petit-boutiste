#!/bin/sh
#
# This Source Code Form is subject to the terms of the Mozilla Public
#  License, v. 2.0. If a copy of the MPL was not distributed with this
#  file, You can obtain one at https://mozilla.org/MPL/2.0/.
#

act pull_request \
  -s GITHUB_TOKEN="$(gh auth token)" \
  -P macos-latest=-self-hosted \
  --container-architecture linux/amd64 \
  -W .github/workflows/validate-pr.yml
