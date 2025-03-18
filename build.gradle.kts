plugins {
    id("java")
    id("application")
}

group = "fun.jaobabus"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.jetbrains:annotations:26.0.2")
}


val mainClass = "fun.jaobabus.commandlib.Main"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    manifest {
        attributes(
            "Main-Class" to mainClass
        )
    }
}

tasks.test {
    useJUnitPlatform()
}