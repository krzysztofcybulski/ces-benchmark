import io.gatling.gradle.LogHttp
import ratpack.gradle.RatpackPlugin

group = "me.kcybulski.ces.benchmark"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.ratpack:ratpack-gradle:2.0.0-rc-1")
    }
}

plugins {
    kotlin("jvm") version "1.8.0"
    id("io.gatling.gradle") version "3.9.2.2"
    idea
}

apply {
    plugin<RatpackPlugin>()
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("me.kcybulski.ces:cloud-store-client:0.0.1")
    implementation("me.kcybulski.ces:event-store-aggregates:0.0.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("net.datafaker:datafaker:1.8.0")
    implementation("io.github.microutils:kotlin-logging:3.0.4")
    implementation("org.slf4j:slf4j-simple:2.0.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

gatling {
    logLevel = "WARN"
    logHttp = LogHttp.NONE

    enterprise.closureOf<Any> {
        // Enterprise Cloud (https://cloud.gatling.io/) configuration reference: https://gatling.io/docs/gatling/reference/current/extensions/gradle_plugin/#working-with-gatling-enterprise-cloud
        // Enterprise Self-Hosted configuration reference: https://gatling.io/docs/gatling/reference/current/extensions/gradle_plugin/#working-with-gatling-enterprise-self-hosted
    }
}