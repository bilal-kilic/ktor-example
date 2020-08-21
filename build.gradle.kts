val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val main_class by extra("io.ktor.server.netty.EngineMain")

plugins {
    application
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
    id("com.google.cloud.tools.jib") version "2.5.0"
}


group = "com.gitub.bilalkilic"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-jackson:1.3.1")
    implementation("org.koin:koin-ktor:2.1.6")
    implementation("com.trendyol:kediatr-core:1.0.12")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("com.couchbase.client:java-client:3.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.3.8-1.4.0-rc")
    implementation("io.dropwizard.metrics:metrics-jmx:4.0.0")
    implementation("com.jtransc:jtransc-rt:0.3.1")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

jib {
    container {
        ports = listOf("8085")
        mainClass = main_class

/*        jvmFlags = listOf(
            "-XX:+UseContainerSupport",
            "-XX:+IdleTuningCompactOnIdle ",
            "-XX:+IdleTuningGcOnIdle",
            "-XX:+UseContainerSupport"
        )*/
    }
}
