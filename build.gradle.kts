plugins {
    id("java")
}

group = "org.JohNils"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.apache.xmlgraphics:batik-transcoder:1.16")
    implementation("org.apache.xmlgraphics:batik-codec:1.16")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.JohNils.Main" // <- your fully qualified main class
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE // or WARN, INCLUDE depending on your needs

    // Optional: include dependencies inside the JAR (fat jar)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
