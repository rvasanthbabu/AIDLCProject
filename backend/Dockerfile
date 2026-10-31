# Build stage
# SECURITY-10/13: use a pinned, non-latest base image. In CI, pin by digest
# (e.g., maven:3.9.9-eclipse-temurin-21@sha256:<verified-digest>) once the digest is resolved.
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
COPY common-core/pom.xml common-core/
COPY platform-identity/pom.xml platform-identity/
COPY app/pom.xml app/
# Pre-fetch dependencies for better layer caching
RUN mvn -q -e -ntp dependency:go-offline || true
COPY . .
RUN mvn -q -e -ntp -DskipTests package

# Runtime stage
# SECURITY-10/13: pin this base image by digest in CI as well.
FROM eclipse-temurin:21-jre-jammy
RUN useradd -r -u 1001 appuser
WORKDIR /app
COPY --from=build /src/app/target/ideation-portal.jar /app/ideation-portal.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ideation-portal.jar"]
