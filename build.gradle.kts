import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

val cssVersion: String by project
val kotlinVersion: String by project
val kotlinxHtmlVersion: String by project
val logbackVersion: String by project
val loggingVersion: String by project
val ktorVersion: String by project
val hikariVersion: String by project
val pgjdbcVersion: String by project
val postgresVersion: String by project
val exposedVersion: String by project

val mainClassName = "com.vapi4k.ApplicationKt"

plugins {
    val kotlinVersion: String by System.getProperties()
    val ktorVersion: String by System.getProperties()
    val versionsVersion: String by System.getProperties()
    id("maven-publish")
    kotlin("jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    id("io.ktor.plugin") version ktorVersion
    id("com.github.ben-manes.versions") version versionsVersion
    //id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

val vstr = "1.0.1"

group = "com.vapi4k"
version = vstr

application {
    mainClass.set("com.vapi4k.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-encoding:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")

    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-compression-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng-all:$pgjdbcVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$loggingVersion")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.vapi4k"
            artifactId = "vapi4k-core"
            version = vstr
            from(components["java"])
        }
    }
}

kotlin {
    jvmToolchain(17)

    sourceSets.all {
        languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
    }
}

tasks {
    register<Jar>("uberJar") {
        archiveClassifier.set("uber")
        from(sourceSets.main.get().output)
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
        manifest { attributes["Main-Class"] = "com.vapi4k.ApplicationKt" }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            listOf("BETA", "-RC").any { candidate.version.uppercase().contains(it) }
        }
    }
}

//tasks.findByName("lintKotlinCommonMain")?.apply {
//    dependsOn("kspCommonMainKotlinMetadata")
//}


//detekt {
//    buildUponDefaultConfig = true // preconfigure defaults
//    allRules = false // activate all available (even unstable) rules.
//    config.setFrom("$projectDir/config/detekt/detekt.yml") // custom config defining rules to run
//    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
//}
//
//tasks.withType<Detekt>().configureEach {
//    reports {
//        html.required.set(true) // observe findings in your browser with structure and code snippets
//        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
//        // similar to the console output, contains issue signature to manually edit baseline files
//        txt.required.set(true)
//        // standardized SARIF format to support integrations with GitHub Code Scanning
//        sarif.required.set(true)
//        md.required.set(true) // simple Markdown format
//    }
//}
