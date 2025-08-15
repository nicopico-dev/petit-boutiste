# Petit Boutiste Development Guidelines

This document provides essential information for developers working on the Petit Boutiste project.

## Build/Configuration Instructions

### Project Setup

Petit Boutiste is a Kotlin Multiplatform project using Compose Multiplatform for the UI. The project is configured to target desktop platforms (JVM).

### Building the Project

To build the project:

```bash
./gradlew build
```

### Running the Application

To run the desktop application:

```bash
./gradlew :composeApp:run
```

### Hot Reload

The project supports Compose Hot Reload for faster development:

```bash
./gradlew :composeApp:composeHotRun
```

## Testing Information

### Test Structure

- Tests are organized in the same package structure as the main code
- Common tests are located in `composeApp/src/commonTest/`
- Platform-specific tests are located in `composeApp/src/<platform>Test/`

### Running Tests

To run all tests:

```bash
./gradlew test
```

To run a specific test:

```bash
./gradlew test --tests "fr.nicopico.petitboutiste.models.HexStringTest"
```

Another example:

```bash
./gradlew test --tests "fr.nicopico.petitboutiste.models.RepresentationTest"
```

### Writing Tests

Tests use the Kotlin Test library. Here's an example of a test class:

```kotlin
class HexStringTest {

    @Test
    fun `normalizes hex string`() {
        // Given a hex string with mixed case and non-hex characters
        val rawHexString = "1a2B3c4D-5e6F"

        // When creating a HexString
        val hexString = HexString(rawHexString)

        // Then the hex string is normalized
        assertEquals("1A2B3C4D5E6F", hexString.hexString)
    }

    // Additional tests...
}
```

Follow the Given-When-Then pattern for clear test structure:
1. **Given**: Set up the test data and conditions
2. **When**: Execute the code being tested
3. **Then**: Assert the expected outcomes

## Additional Development Information

### Project Structure

- `composeApp/`: Main application module
  - `src/commonMain/`: Cross-platform code
    - `kotlin/fr/nicopico/petitboutiste/`: Main package
      - `models/`: Data models
        - `representation/`: DataRenderer, arguments, decoders (binary, hex, integer, text, protobuf)
      - `ui/`: UI components and screens
        - `components/representation/`: Renderer UI (ArgumentInput, RendererForm, ByteItemRender)
  - `src/desktopMain/`: Desktop-specific code

### Key Components

#### Models

- `HexString`: Represents a normalized hexadecimal string
- `ByteItem`: Sealed class representing either a single byte or a group of bytes
  - `ByteItem.Single`: Represents a single byte (two hex characters)
  - `ByteItem.Group`: Represents a named group of bytes

#### Representations

- `DataRenderer`: Enum that converts a byte array into human-readable forms (Binary, Hexadecimal, Integer, Text, Protobuf)
  - Supports renderer-specific arguments (e.g., Endianness, Charset)
  - `Protobuf` renderer may require user validation before rendering
- Decoders: `decodeBinary`, `decodeHexadecimal`, `decodeInteger`, `decodeText`, `decodeProtobuf`

#### UI Components

The UI is built with Compose Multiplatform and includes components for displaying and inputting hexadecimal data:

- `HexDisplay`: Displays hexadecimal data
- `HexInput`: Allows input of hexadecimal data
- `ByteItemRender`: Renders a list of ByteItem elements (single bytes and groups)
- `ArgumentInput`: Inputs for renderer arguments (e.g., endianness, charset)
- `RendererForm`: Form to select a DataRenderer and configure its arguments

### Code Style

- Follow Kotlin coding conventions
- Use sealed classes for representing different variants of a type
- Validate input in model classes' initializers
- Use extension functions for adding functionality to existing classes
- Write tests for all business logic
