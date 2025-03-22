# Marketcetera

Marketcetera is an open-source trading platform that provides advanced capabilities for financial markets trading.

## Prerequisites

- JDK 11 (Oracle JDK or OpenJDK)
- Maven 3.6+
- Git

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/marketcetera/marketcetera.git
cd marketcetera
```

### Build the Project

The Marketcetera project uses Maven for build management. To build the entire project:

```bash
mvn clean install
```

To skip tests during the build (faster):

```bash
mvn clean install -DskipTests
```

### Building Specific Modules

You can build specific modules by using the `-pl` (project list) parameter:

```bash
# Build the core module
mvn clean install -pl core

# Build multiple modules
mvn clean install -pl rpc-core,admin/admin-rpc-core,dataflow/dataflow-rpc-core
```

### Eclipse Integration

Marketcetera is configured to work with Eclipse IDE. There are two ways to work with Eclipse:

#### Option 1: Direct Maven Project Import (Recommended)

1. From Eclipse, use "Import > Existing Maven Projects"
2. Navigate to the root directory of marketcetera
3. Select the projects you want to import

With this approach, Eclipse's M2E plugin will handle the Maven integration directly.

#### Option 2: Generate Eclipse Project Files

Alternatively, you can use Maven to generate Eclipse project files:

1. Generate Eclipse project files:

```bash
mvn eclipse:eclipse
```

2. Import the projects into Eclipse using "Import > Existing Projects into Workspace"

With either approach, the project is configured to use separate build directories:
- Maven builds to: `target/`
- Eclipse builds to: `target-eclipse/classes`

This separation prevents conflicts between Maven and Eclipse builds.

## Protocol Buffers and gRPC

Marketcetera uses Protocol Buffers and gRPC for communication. The source files are located in:

- `src/main/proto` - Main protocol buffer definitions
- `src/test/proto` - Test protocol buffer definitions

To regenerate the Protocol Buffer source files:

```bash
mvn clean compile
```

## Key Modules

- **core**: Core components and interfaces
- **rpc-core**: Base RPC functionality
- **admin**: Admin services and APIs
- **dare**: Deploy Anywhere Routing Engine
- **fix**: FIX protocol implementation
- **trade**: Trading functionality
- **marketdata**: Market data services

## Running Tests

To run all tests:

```bash
mvn test
```

To run tests for a specific module:

```bash
mvn test -pl <module-name>
```

## Documentation

For more detailed information and documentation, see:

- [Marketcetera Documentation](https://www.marketcetera.org/docs/)
- [Wiki](https://github.com/marketcetera/marketcetera/wiki)

## License

Marketcetera is licensed under the GNU General Public License (GPL). See the [LICENSE](LICENSE) file for details.