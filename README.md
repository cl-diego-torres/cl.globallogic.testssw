# Testssw

Test microservice developed to handle the creation and access of user accounts.

### Tech Stack

* Java 11
* SpringBoot 2.5.14
* Gradle 7.4

### UML Diagrams

* [Components diagram](diagrams/testssw-component.pdf)
* [Sequence diagram](diagrams/testssw-sequence.pdf)

### Installation

The repository provides an embedded Gradle for quick installation.

1. Run the build command to execute tests and generate an executable JAR:
```bash
./gradlew build
```
2. Once all tests have passed, you can run the application using the bootRun task:
```bash
./gradlew bootRun
```
3. Alternatively, in the repository, you can find a Dockerfile to run the application in a simple container. Build the Docker image using the following command:
```bash
docker build -t globallogic/testssw .
```
4. Finally, run the Docker container:
```bash        
docker run -p 8080:8080 -d globallogic/testssw
```

### 