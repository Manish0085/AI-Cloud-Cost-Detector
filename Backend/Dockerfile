# =============================================================================
# Backend — Spring Boot 3.5 / Java 21  (multi-stage, non-root, slim runtime)
# =============================================================================

# ---- Stage 1: build the fat jar with Maven -------------------------------- #
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Cache dependencies: copy pom first, resolve, THEN copy sources.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
# Tests run in the Jenkins pipeline, not in the image build.
RUN mvn -B -q clean package -DskipTests \
 && cp target/*.jar /build/app.jar

# ---- Stage 2: minimal runtime --------------------------------------------- #
FROM eclipse-temurin:21-jre AS runtime

# curl is used by the container HEALTHCHECK only.
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --system --gid 1001 appuser \
 && useradd  --system --uid 1001 --gid appuser --home /app appuser

WORKDIR /app

# Spring AI transformers downloads an ONNX embedding model at first start;
# give it a writable, persistable cache dir (mounted as a volume in compose).
ENV MODEL_CACHE=/app/.cache
RUN mkdir -p /app/.cache /app/tmp && chown -R appuser:appuser /app

COPY --from=build --chown=appuser:appuser /build/app.jar /app/app.jar

USER appuser
EXPOSE 8081

# Container-level liveness; orchestration also checks this in compose.
HEALTHCHECK --interval=30s --timeout=5s --start-period=120s --retries=5 \
  CMD curl -fsS http://localhost:8081/actuator/health || exit 1

# Container-aware heap (uses cgroup limits) + writable tmp for the ONNX model.
ENTRYPOINT ["sh","-c","java -XX:MaxRAMPercentage=70 -Djava.io.tmpdir=/app/tmp -jar /app/app.jar"]