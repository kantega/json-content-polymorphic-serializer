plugins {
    application
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization").version("1.6.21")
}

application {
    mainClass.set("ServerKt")
}

repositories {
    mavenLocal()

    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-netty:1.6.3")
    implementation("io.ktor:ktor-html-builder:1.6.3")
    implementation("io.ktor:ktor-serialization:1.6.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.3")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
