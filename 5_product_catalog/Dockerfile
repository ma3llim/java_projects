FROM gradle:9.6.1-jdk21 AS gradle

WORKDIR /app

# Copy Gradle files
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle .
COPY gradle ./gradle

COPY app/build.gradle ./app/build.gradle
COPY app/src ./app/src

# Build the spring boot application
RUN ./gradlew :app:clean :app:bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre
LABEL maintainer="Mohd Sameer"

WORKDIR /app/product-catalog

# Create a non-root user
RUN groupadd --system spring && \
    useradd --system --gid spring --create-home spring

# Copy only the generated JAR
COPY --from=gradle /app/app/build/libs/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]