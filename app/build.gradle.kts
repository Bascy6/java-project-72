plugins {
    checkstyle
    application
    jacoco
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.6"
    id("com.adarshr.test-logger") version "3.0.0"
    id("com.github.ben-manes.versions") version "0.50.0"
}

application {
    mainClass.set("hexlet.code.App")
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("io.javalin:javalin-rendering:6.1.6")
    implementation("gg.jte:jte:2.3.2")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.konghq:unirest-java-core:4.4.4")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("org.postgresql:postgresql:42.7.3")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.javalin:javalin-bundle:6.3.0")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }
