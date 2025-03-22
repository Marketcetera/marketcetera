# Eclipse Integration Guide

## Build Directory Configuration

This project is configured to use separate build directories for Maven and Eclipse to prevent conflicts:

* Maven builds into `target/`
* Eclipse builds into `target-eclipse/`

This is achieved through multiple configuration methods:

1. **M2E Profile in root pom.xml**
   - A specific `m2e` profile is activated when working within Eclipse
   - This profile sets `<directory>target-eclipse</directory>` for m2e builds

2. **Eclipse JDT Settings**
   - Project-wide settings in `.settings/org.eclipse.jdt.core.prefs`
   - Sets `output.directory=target-eclipse/classes`

3. **Eclipse Core Resource Settings**  
   - Project-wide settings in `.settings/org.eclipse.core.resources.prefs`
   - Sets `eclipse.output.directory=target-eclipse`

## Importing the Project

To properly import this project into Eclipse:

1. Import as Maven project
   - Use "Import > Existing Maven Projects"
   - Select the root directory containing the pom.xml

2. Clean and rebuild the project
   - Right-click the project > Maven > Update Project
   - Check "Clean projects" option
   - Click OK

## Verifying Build Directory

To verify Eclipse is building to the correct directory:

1. Right-click a project > Properties
2. Go to "Java Build Path" > Source tab
3. Check that "Default output folder" shows "target-eclipse/classes"

## Troubleshooting

If Eclipse is still building to the `bin/` directory:

1. Run "Maven > Update Project" again with "Clean projects" checked
2. Check for errors in the Eclipse Error Log
3. Ensure the `.settings` directory is properly recognized by Eclipse