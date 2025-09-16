# Colorado RLA Software - API Server

## Requirements

Before you get started, you will need:

- Java 8 or higher
- Maven

## Quick start

1. Install the dependencies under **Requirements**.
2. Build a JAR:
   ```
   $ mvn package
   ```
3. Run the JAR:
   ```
   $ java -jar target/corla-server-${VERSION}-shaded.jar
   ```

## Development

Common development tasks:

- `mvn package`: build the system, including a self-contained JAR
- `mvn test`: run the test suite
- `mvn verify`: run the code quality checks

## Tests

Unit tests can be run from the command line:

```sh
mvn test
```

By default, integration tests requiring a database are excluded. To avoid
excluding those tests, you can override the excluded groups from the command
line:

```sh
mvn test -Dcorla.test.excludedGroups=""
```
