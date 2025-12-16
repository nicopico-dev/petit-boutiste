# Petit Boutiste

![screenshot.png](screenshot.png)

## General purpose of the project

Petit Boutiste is a desktop tool to explore and understand raw byte data. Paste or type data (typically in hexadecimal or binary), define meaningful byte groups, and instantly view multiple human‑readable representations such as integers, text, and other. Built with Kotlin Multiplatform and Compose Multiplatform, it focuses on a fast, convenient workflow for day‑to‑day binary inspection.

## Features

- Input and display data in hexadecimal or binary format
- Quick toggle between HEX, BIN, and BASE64 input modes
- Define named byte groups and visualize selections
- Render groups as:
  - Raw hexadecimal
  - Binary
  - Integer values (configurable endianness, signed/unsigned)
  - Text (configurable charset and endianness)
  - Protocol Buffers using a `.desc` file, e.g.:
    - `protoc --include_imports foo.proto --descriptor_set_out=foo.desc`
  - Custom rendering logic with Kotlin Script (KTS)
  - Sub-template for repeating elements
- Save, load, export, and import templates to reuse structures
- Multi‑tab interface to work on several datasets at once

## Development

### Prerequisites

- JDK 11 or later

### Build

```bash
./gradlew build
```

### Run (desktop)

```bash
./gradlew :composeApp:run
```

### Hot reload (Compose)

```bash
./gradlew :composeApp:composeHotRun
```

### Create a distributable

```bash
./gradlew :composeApp:createReleaseDistributable
```

Packages are generated for the host OS (macOS: APP, Windows: EXE, Linux: DEB/RPM).

### Tests

- Run all tests:

```bash
./gradlew test
```

- Run a specific test (example):

```bash
./gradlew test --tests "fr.nicopico.petitboutiste.models.HexStringTest"
```

Test sources follow the main package layout. Common tests are in `composeApp/src/commonTest/`; platform‑specific ones are in `composeApp/src/<platform>Test/`.

## License
This project is licensed under the [MPL-2.0 License](LICENSE)
