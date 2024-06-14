import community.flock.wirespec.compiler.core.emit.KotlinEmitter
import community.flock.wirespec.compiler.core.emit.shared.KotlinShared
import community.flock.wirespec.compiler.core.emit.transformer.ClassModelTransformer.transform
import community.flock.wirespec.compiler.core.parse.AST
import community.flock.wirespec.compiler.core.parse.Refined
import community.flock.wirespec.compiler.core.parse.Type
import community.flock.wirespec.plugin.Language
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "2.0.0-RC3"
    id("io.ktor.plugin") version "2.3.9"
    id("community.flock.wirespec.plugin.gradle") version "0.0.0-SNAPSHOT"
}

group = "community.flock.wirespec.example-gradle_plugin"
version = "0.0.0-SNAPSHOT"

application {
    mainClass.set("community.flock.wirespec.examples.app.ApplicationKt")
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0-RC")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.withType<KotlinCompile> {
    dependsOn("wirespec")
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

sourceSets {
    main {
        java {
            srcDir("${layout.buildDirectory.get()}/generated")
        }
    }
}

buildscript {
    dependencies {
        classpath("community.flock.wirespec.compiler:core-jvm:0.0.0-SNAPSHOT")
    }
}

wirespec {
    compile {
        input = "$projectDir/src/main/wirespec"
        output = "${layout.buildDirectory.get()}/typescript"
        packageName = ""
        languages = listOf(Language.TypeScript)
    }

    custom {
        input = "$projectDir/src/main/wirespec"
        output = "${layout.buildDirectory.get()}/generated"
        packageName = "community.flock.wirespec.generated.kotlin"
        emitter = KotlinSerializableEmitter()
        shared = KotlinShared
        extention = "kt"
    }
}

class KotlinSerializableEmitter : KotlinEmitter("community.flock.wirespec.generated.kotlin") {

    override fun Type.emit(ast: AST) = """
    |@kotlinx.serialization.Serializable
    |${transform(ast).emit()}
    """.trimMargin()

    override fun Refined.emit() = """
    |@kotlinx.serialization.Serializable
    |${transform().emit()}
    """.trimMargin()
}
