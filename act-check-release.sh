#!/bin/sh
act -P macos-latest=-self-hosted -W .github/workflows/macos-release.yml -e .github/act/act_release_event.json --artifact-server-path /tmp/act
