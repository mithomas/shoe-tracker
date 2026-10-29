# Stage 1: Build Quarkus application using Gradle on JDK 25
FROM eclipse-temurin:25-jdk AS build

WORKDIR /code

# Copy and trust local/corporate CA certificates if provided in build context
COPY .docker/certs/ /tmp/certs/
RUN for cert in /tmp/certs/*.crt; do \
      if [ -f "$cert" ]; then \
        keytool -importcert -noprompt -cacerts -storepass changeit -file "$cert" -alias "$(basename "$cert")" || true; \
      fi; \
    done

# Copy Gradle wrapper and build configuration
COPY gradle/ /code/gradle/
COPY .editorconfig gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts /code/
RUN chmod +x ./gradlew && ./gradlew --version --no-daemon

# Copy source code and build Quarkus fast-jar
COPY src/ /code/src/
ARG GIT_COMMIT=""
ENV GIT_COMMIT=${GIT_COMMIT}
RUN ./gradlew build -x test --no-daemon

# Stage 2: Minimal runnable JRE 25 image
FROM eclipse-temurin:25-jre

ENV LANGUAGE='en_US:en' \
    QUARKUS_HTTP_HOST=0.0.0.0 \
    QUARKUS_HTTP_PORT=8080 \
    QUARKUS_DATASOURCE_JDBC_URL=jdbc:sqlite:/data/shoe-tracker.db?journal_mode=WAL&busy_timeout=5000 \
    JAVA_TOOL_OPTIONS="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Setup application directory and non-root user (UID 185) with permissions for /data
RUN mkdir -p /deployments /data \
    && chown -R 185:185 /deployments /data \
    && chmod -R 775 /deployments /data

WORKDIR /deployments

# Copy Quarkus fast-jar artifacts from build stage
COPY --from=build --chown=185:185 /code/build/quarkus-app/lib/ /deployments/lib/
COPY --from=build --chown=185:185 /code/build/quarkus-app/*.jar /deployments/
COPY --from=build --chown=185:185 /code/build/quarkus-app/app/ /deployments/app/
COPY --from=build --chown=185:185 /code/build/quarkus-app/quarkus/ /deployments/quarkus/

VOLUME /data
EXPOSE 8080
USER 185

ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]
