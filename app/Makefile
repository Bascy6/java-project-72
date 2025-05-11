# Makefile

install:
	./gradlew install

run-dist:
	# Очистка от результатов предыдущей сборки
	./gradlew clean
	# Создание jar исполняемого файла
	./gradlew installDist

test:
	./gradlew test

report:
	./gradlew jacocoTestReport