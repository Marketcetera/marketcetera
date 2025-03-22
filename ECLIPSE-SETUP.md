# Eclipse Integration Setup

This document provides instructions for importing the Marketcetera projects into Eclipse with proper support for Protocol Buffers.

## Prerequisites

1. Eclipse IDE for Java Developers (recommended Eclipse 2022-12 or later)
2. M2Eclipse plugin (usually included with Eclipse for Java Developers)
3. Optional: Eclipse m2e connector for the protobuf-maven-plugin

## Import Options

### Option 1: Direct Import with M2Eclipse (Recommended)

This approach uses Eclipse's built-in Maven integration:

1. First, generate the Protocol Buffer files:
   ```
   mvn clean generate-sources
   ```

2. In Eclipse, select:
   - File → Import → Maven → Existing Maven Projects
   - Browse to the `/Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera` directory
   - Select all the pom.xml files you want to import
   - Check "Add project(s) to working set" if desired
   - Click Finish

3. Once imported, Eclipse will build the projects. If you see compilation errors related to missing Protocol Buffer classes:
   - Right-click on each project with errors → Maven → Update Project
   - Ensure "Force Update of Snapshots/Releases" is checked
   - Click OK

4. To use separate output directories for Eclipse:
   - Right-click on the project → Properties → Maven → Active Maven Profiles
   - Add "eclipse" to the list of active profiles
   - Click Apply and Close

### Option 2: Generate Eclipse Projects with maven-eclipse-plugin

If direct import doesn't work well, you can use the maven-eclipse-plugin approach:

1. Generate Eclipse project files:
   ```
   mvn clean eclipse:eclipse -DdownloadSources=true
   ```

2. In Eclipse, select:
   - File → Import → General → Existing Projects into Workspace
   - Browse to the `/Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera` directory
   - Select all the projects you want to import
   - Click Finish

3. If you encounter compilation errors:
   - Right-click on the project → Build Path → Configure Build Path
   - Make sure the generated-sources directories are included:
     - `target/generated-sources/protobuf/java`
     - `target/generated-sources/protobuf/grpc-java`

## Protobuf Development

When working with Protocol Buffer files (.proto), follow these steps:

1. After modifying .proto files, run:
   ```
   mvn generate-sources
   ```

2. In Eclipse, refresh the project:
   - Right-click on the project → Refresh
   - Eclipse should pick up the newly generated files

3. If Eclipse doesn't recognize the generated sources:
   - Right-click on the project → Maven → Update Project

## Troubleshooting

### Missing Protocol Buffer Classes

If Eclipse cannot find the generated Protocol Buffer classes:

1. Verify the classes were generated:
   ```
   ls -la target/generated-sources/protobuf/java
   ```

2. Make sure the build-helper-maven-plugin is properly configured in the pom.xml

3. Try manually adding the generated sources to your build path:
   - Right-click on project → Build Path → Configure Build Path
   - Add the `target/generated-sources/protobuf/java` directory as a source folder

### Compiler Output Conflicts

If you encounter conflicts between Maven and Eclipse compilations:

1. Activate the 'eclipse' profile to use separate output directories:
   ```
   mvn -Peclipse clean install
   ```

2. In Eclipse, configure the project to use the 'eclipse' profile:
   - Right-click on project → Properties → Maven → Active Maven Profiles
   - Add "eclipse" to the list
   - Click Apply and Close