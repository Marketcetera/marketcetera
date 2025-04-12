# GitHub CI/CD Configuration for marketcetera

This directory contains GitHub-specific configurations for Continuous Integration/Continuous Deployment (CI/CD) of the marketcetera project.

## Directory Structure

- `.github/workflows/` - Contains GitHub Actions workflow definitions
- `.github/workflows/README.md` - Documentation for workflows
- `.github/workflows/PROPERTIES.md` - Documentation on database property handling

## CI/CD Overview

The CI/CD pipelines in this repository are implemented using GitHub Actions. The main workflows include:

1. **Java CI with Maven** (`ci.yml`) - Builds and tests the project
   - Triggered on push to main branches and pull requests
   - Sets up Java 17 environment
   - Configures database properties via environment variables
   - Builds and tests the Java code
   - Generates test reports
   - Uploads build artifacts

## Database Configuration

For CI/CD builds, the database is configured using environment variables that are converted to Maven properties. This approach:

- Eliminates the need for a `settings.xml` file in CI
- Follows security best practices for CI/CD environments
- Works with H2 in-memory database for testing

For more details, see the [PROPERTIES.md](./workflows/PROPERTIES.md) file.

## Helper Scripts

The following helper scripts are included in the workflows directory:

- `verify-env.sh` - Verifies that all required environment variables are set
- `property-resolver.sh` - Converts environment variables to Maven properties

## Adding New Workflows

When adding new workflows, please:

1. Create the workflow file in the `.github/workflows` directory
2. Add documentation in the README.md file
3. Follow existing patterns for environment variables and caching

## GitHub Actions Limitations

Notable GitHub Actions limitations to be aware of:

- GitHub-hosted runners have usage limits
- Secrets are not available in workflows triggered by forks
- Artifacts have retention limits

## Related Projects

This CI/CD configuration serves the marketcetera project, which is related to:

- metc-sixer project
- enterprise project 

These projects may require similar CI/CD setups with appropriate adaptations for their specific needs.