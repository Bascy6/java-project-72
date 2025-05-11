FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Копируем только необходимые файлы для кэширования
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./

# Скачиваем зависимости (использует кэш слоев)
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код
COPY src src

# Собираем приложение
RUN ./gradlew shadowJar --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/app.jar"]