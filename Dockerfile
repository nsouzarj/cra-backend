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
RUN mvn clean package -DskipTests

# Debug: List files in target directory
RUN ls -la /app/target/

# Install native font libraries (resolves UnsatisfiedLinkError for libfreetype.so.6)
RUN apt-get update && \
    apt-get install -y libfreetype6 libfontconfig1 && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Create upload directory with correct permissions
RUN mkdir -p /app/uploads && \
    chmod 755 /app/uploads

# Declare volume for persistent file storage
VOLUME ["/app/uploads"]

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user and group
RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 --ingroup spring --no-create-home spring

# Change ownership of /app (including app.jar and uploads)
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8081

# Run the application with properties for JasperReports and file uploads
ENTRYPOINT ["java", "-Dnet.sf.jasperreports.compiler.temp.dir=/tmp", "-Dfile.upload-dir=/app/uploads", "-jar", "app.jar"]