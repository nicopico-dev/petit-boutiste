# Petit Boutiste

Petit Boutiste is a desktop application for analyzing and interpreting hexadecimal and binary data. It provides a user-friendly interface for working with binary data formats, allowing you to define and visualize byte groups with different representations.

![screenshot.png](screenshot.png)

## Features

- Input and display data in hexadecimal or binary format
- Toggle between hex and binary input modes
- Define named groups of bytes
- Interpret byte groups as different data types:
  - Raw hexadecimal
  - Integer values (with configurable endianness)
  - Text (with configurable endianness)
- Save and load templates for reuse with similar data structures
- Export and import templates for sharing
- Multi-tab interface for working with multiple data sets simultaneously

## Getting Started

### Prerequisites

- JDK 11 or later

### Running the Application

You can run the application using Gradle:

```bash
./gradlew :composeApp:run
```

For faster development with hot reload:

```bash
./gradlew :composeApp:composeHotRun
```

### Building a distributable application

You can build a distributable application for the OS running the build:

```bash
./gradlew :composeApp:createReleaseDistributable
```

This will create packages in the following formats depending on your OS:
- macOS: APP
- Windows: EXE
- Linux: DEB/RPM

## Testing

### Running Tests

To run all tests:

```bash
./gradlew test
```

To run a specific test:

```bash
./gradlew test --tests "fr.nicopico.petitboutiste.models.HexStringTest"
```

### Test Structure

- Tests are organized in the same package structure as the main code
- Common tests are located in `composeApp/src/commonTest/`
- Platform-specific tests are located in `composeApp/src/<platform>Test/`

## Project Structure

### Key Components

#### Models

- `DataString`: Interface for different string representations (hex, binary)
- `HexString`: Represents a normalized hexadecimal string
- `BinaryString`: Represents a normalized binary string (sequence of 0s and 1s)
- `ByteItem`: Sealed class representing either a single byte or a group of bytes
  - `ByteItem.Single`: Represents a single byte (two hex characters)
  - `ByteItem.Group`: Represents a named group of bytes
- `ByteGroupDefinition`: Defines a group of bytes with a name and representation format
- `RepresentationFormat`: Defines how a byte group should be interpreted
  - `Hexadecimal`: Raw hex representation
  - `Integer`: Numeric interpretation with configurable endianness
  - `Text`: Text interpretation with configurable charset and endianness
- `InputType`: Enum defining supported input types (HEX, BINARY)
- `TabId`: Value class representing a unique identifier for a tab
- `TabData`: Represents the data for a single tab, including its input data, input type, and group definitions

#### UI Components

- `AppScreen`: Main application screen with multi-pane layout
- `HexInput`: Component for inputting hexadecimal data
- `BinaryInput`: Component for inputting binary data
- `InputTypeToggle`: Component for switching between hex and binary input modes
- `HexDisplay`: Component for displaying hexadecimal data with byte groups
- `ByteGroupDefinitions`: Component for managing byte group definitions
- `TemplateManagement`: Component for saving, loading, and sharing templates
- `TabBar`: Component for managing multiple data tabs

### Code Organization

- `fr.nicopico.petitboutiste`
  - `models`: Data models and business logic
  - `repository`: Data persistence and management
  - `ui`: User interface components
    - `components`: Reusable UI components
    - `infra`: Infrastructure code for UI (previews, state savers)

## Development

### Technology Stack

- Kotlin Multiplatform
- Compose Multiplatform for UI
- Kotlinx Serialization for JSON serialization
- Multiplatform Settings for preferences

### Building from Source

1. Clone the repository
2. Open the project in IntelliJ IDEA or Android Studio
3. Build and run using the Gradle tasks mentioned above
