plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
}

subprojects {
    if (listOf("applications", "components", "support").contains(name)) return@subprojects

    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.7")

        testImplementation(kotlin("test-junit"))
        implementation("org.jetbrains.exposed:exposed-core:0.41.1")
        implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
        implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
        implementation("org.postgresql:postgresql:42.5.0")
        implementation("io.micrometer:micrometer-registry-prometheus:1.10.2")

    }
}