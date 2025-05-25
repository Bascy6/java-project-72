FROM eclipse-temurin:20-jdk

RUN apt-get update && apt-get install -yq unzip

WORKDIR /app

COPY app .

RUN chmod +x gradlew

RUN ./gradlew shadowJar

CMD java -jar build/libs/java-project-72-all.jar