# Latency Test Java Web Application

This is a Java web application designed to measure latency between HTTP calls. The application consists of a servlet that can be deployed on a servlet container like Apache Tomcat or WildFly (formerly JBoss). The servlet exposes an endpoint `/latencytest` to perform latency testing.

## Features

- **Latency Measurement**: Measures latency between HTTP calls to a specified URL.
- **Configurable**: Allows configuration of the URL, number of calls, and delay between calls.
- **JSON Response**: Returns latency data in JSON format, including average latency.

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven (for building the WAR file)
- Docker (optional, for containerized deployment)

## Installation

### Building the WAR file

1. Clone this repository to your local machine.
2. Navigate to the project directory.
3. Build the WAR file using Maven:
    ```bash
    mvn clean package
    ```
4. The WAR file will be generated in the `target` directory.

### Deploying on Apache Tomcat

1. Ensure you have Docker installed.
2. Build the Docker image using the provided Dockerfile:
    ```bash
    docker build -t latency-test-tomcat -f Dockerfile.tomcat .
    ```
3. Run the Docker container:
    ```bash
    docker run -p 8080:8080 latency-test-tomcat
    ```

### Deploying on WildFly (JBoss)

1. Ensure you have Docker installed.
2. Build the Docker image using the provided Dockerfile:
    ```bash
    docker build -t latency-test-wildfly -f Dockerfile.wildfly .
    ```
3. Run the Docker container:
    ```bash
    docker run -p 8080:8080 latency-test-wildfly
    ```

## Usage

Once the application is deployed, you can access the latency testing endpoint at:


ChatGPT
markdown
Copy code
# Latency Test Java Web Application

This is a Java web application designed to measure latency between HTTP calls. The application consists of a servlet that can be deployed on a servlet container like Apache Tomcat or WildFly (formerly JBoss). The servlet exposes an endpoint `/latencytest` to perform latency testing.

## Features

- **Latency Measurement**: Measures latency between HTTP calls to a specified URL.
- **Configurable**: Allows configuration of the URL, number of calls, and delay between calls.
- **JSON Response**: Returns latency data in JSON format, including average latency.

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven (for building the WAR file)
- Docker (optional, for containerized deployment)

## Installation

### Building the WAR file

1. Clone this repository to your local machine.
2. Navigate to the project directory.
3. Build the WAR file using Maven:
    ```bash
    mvn clean package
    ```
4. The WAR file will be generated in the `target` directory.

### Deploying on Apache Tomcat

1. Ensure you have Docker installed.
2. Build the Docker image using the provided Dockerfile:
    ```bash
    docker build -t latency-test-tomcat -f Dockerfile.tomcat .
    ```
3. Run the Docker container:
    ```bash
    docker run -p 8080:8080 latency-test-tomcat
    ```

### Deploying on WildFly (JBoss)

1. Ensure you have Docker installed.
2. Build the Docker image using the provided Dockerfile:
    ```bash
    docker build -t latency-test-wildfly -f Dockerfile.wildfly .
    ```
3. Run the Docker container:
    ```bash
    docker run -p 8080:8080 latency-test-wildfly
    ```

## Usage

Once the application is deployed, you can access the latency testing endpoint at: `http://localhost:8080/latencytest`

### Parameters

- **url**: The URL to test latency against.
- **numCalls**: The number of HTTP calls to make.
- **delayInMillis**: Delay between each call in milliseconds.

Example:
```bash
curl -X POST \
  'http://{{url}}/latencytest?url={{url}}/latencytest/latencytest&numCalls=20&delayInMillis=0' \
  --header 'Accept: */*'
```

- Replace {{url}} with the actual URL you want to test latency against.