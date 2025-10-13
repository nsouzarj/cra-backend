# Multi-stage build: Build stage
FROM openjdk:23-jdk-slim AS builder

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package

# Debug: List files in target directory
RUN ls -la /app/target/

# Runtime stage
FROM openjdk:23-jre-slim

# Install native font libraries (resolves UnsatisfiedLinkError for libfreetype.so.6)
RUN apt-get update && \
    apt-get install -y libfreetype6 libfontconfig1 && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Create upload directory
RUN mkdir -p /app/uploads && \
    chmod 755 /app/uploads

# Declare volume for persistent file storage
VOLUME ["/app/uploads"]

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/cra-backend-0.0.1-SNAPSHOT.jar app.jar

# Create non-root user and group
RUN addgroup --system spring && \
    adduser --system spring --ingroup spring

# Change ownership of /app
RUN chown -R spring:spring /app

# Expose port
EXPOSE 8081

USER spring:spring

# Run the application
ENTRYPOINT ["java", "-Dnet.sf.jasperreports.compiler.temp.dir=/tmp", "-Dfile.upload-dir=/app/uploads", "-jar", "app.jar"]