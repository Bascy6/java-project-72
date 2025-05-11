# Базовый образ с Java 21
FROM eclipse-temurin:21-jdk-jammy

# Рабочая директория
WORKDIR /app

# Копируем Gradle файлы
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Копируем исходный код
COPY src src

# Собираем приложение
RUN ./gradlew shadowJar --no-daemon

# Порт приложения
EXPOSE 7070

# Команда запуска
CMD ["java", "-jar", "build/libs/app.jar", "--port", "7070"]