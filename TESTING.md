# Testing in CRA Backend

This project uses JUnit 5 and Mockito for testing. The Maven Surefire plugin generates test reports that can be used to analyze test execution.

## Running Tests

### Basic Test Execution
```bash
mvn test
```

### Running Specific Tests
```bash
# Run a specific test class
mvn test -Dtest=GoogleDriveServiceTest

# Run a specific test method
mvn test -Dtest=GoogleDriveServiceTest#testSetTokens_ShouldSetTokensCorrectly
```

## Test Reports

After running tests, Maven generates reports in:
```
target/surefire-reports/
```

These reports include:
- XML files with detailed test results
- Plain text files with test execution output

## Test Analysis

The project includes comprehensive unit tests for services:
- GoogleDriveServiceTest
- DashboardServiceTest
- EmailServiceTest
- AuthServiceTest
- And other service tests

## Code Coverage Alternative

Since JaCoCo has been intentionally removed from the project configuration, you can use external tools for code coverage analysis:

1. **IntelliJ IDEA**: Built-in code coverage tool
2. **Eclipse**: EclEmma plugin
3. **SonarQube**: Static analysis platform with coverage analysis
4. **Clover**: Commercial code coverage tool

## Test Quality Guidelines

1. **Mocking**: Use Mockito for isolating units under test
2. **Assertions**: Use JUnit 5 assertions for clear test validation
3. **Test Naming**: Follow the pattern `methodName_StateUnderTest_ExpectedBehavior`
4. **Test Structure**: Follow the AAA pattern (Arrange, Act, Assert)
5. **Coverage**: Aim for meaningful coverage rather than just percentage targets

## Common Test Commands

```bash
# Clean and run all tests
mvn clean test

# Run tests with verbose output
mvn test -X

# Skip tests during build
mvn install -DskipTests

# Run tests without compiling (if already compiled)
mvn surefire:test
```