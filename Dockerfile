# Syntax directive for modern BuildKit features
# syntax=docker/dockerfile:1

# --- Stage 1: Resolve Dependencies ---
FROM eclipse-temurin:26-jdk-jammy AS deps
WORKDIR /build

# Copy Maven wrapper assets
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

# Mount pom.xml and download dependencies offline
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests

# --- Stage 2: Compile and Package ---
FROM deps AS package
WORKDIR /build

# Copy the source code
COPY src/ src/

# Bind pom.xml, leverage .m2 cache, build, and dynamically rename the jar safely
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    JAR_NAME=$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar && \
    mv target/"$JAR_NAME" target/app.jar

# --- Stage 3: Extract Spring Boot Layers ---
FROM eclipse-temurin:26-jre-jammy AS extract
WORKDIR /build
# Copy the compiled jar from the package stage
COPY --from=package /build/target/app.jar target/app.jar
# Extract using layertools
RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

# --- Stage 4: Final Production Runtime ---
FROM eclipse-temurin:26-jre-jammy AS final

# Standard production security: Create a non-root system user
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

# Configure optimal production JVM defaults for containers
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseContainerSupport"
WORKDIR /app
USER appuser

# Copy extracted layers with proper ownership in the correct order of change frequency
COPY --from=extract --chown=appuser:appuser /build/target/extracted/dependencies/ ./
COPY --from=extract --chown=appuser:appuser /build/target/extracted/spring-boot-loader/ ./
COPY --from=extract --chown=appuser:appuser /build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract --chown=appuser:appuser /build/target/extracted/application/ ./

EXPOSE 8080

# Execute using Spring Boot's native layer launcher
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher" ]