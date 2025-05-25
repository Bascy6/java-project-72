FROM eclipse-temurin:20-jdk

RUN apt-get update && apt-get install -yq unzip

WORKDIR /app

COPY app/build.gradle.kts .
COPY app/settings.gradle.kts .
COPY app/gradle ./gradle
COPY app/gradlew .
COPY app/src ./src

RUN chmod +x gradlew

RUN ./gradlew shadowJar

CMD ["java", "-cp", "build/libs/java-project-72-all.jar:build/libs/*", "hexlet.code.App"]