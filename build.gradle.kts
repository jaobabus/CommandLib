plugins {
    id("java")
    id("application")
    id("maven-publish")
}

group = "fun.jaobabus"
version = "0.2.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

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

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to mainClass
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "fun.jaobabus"
            artifactId = "commandlib"
            version = project.version.toString()

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "myRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
        mavenLocal()
    }
}