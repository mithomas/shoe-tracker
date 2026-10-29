import java.util.zip.ZipFile

plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.allopen") version "2.4.10"
    id("io.quarkus")
    id("com.diffplug.spotless") version "7.0.2"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

val closureCompiler by configurations.creating

dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-agroal")
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-jdbc-h2")
    implementation("io.quarkus:quarkus-jdbc-mariadb")
    implementation("net.java.dev.jna:jna")
    implementation("net.java.dev.jna:jna-platform")
    implementation("com.fasterxml.uuid:java-uuid-generator:5.2.0")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("org.mvnpm:leaflet:1.9.4")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.assertj:assertj-core:3.27.3")

    closureCompiler("com.google.javascript:closure-compiler:v20240317")
}
group = "org.shoetracker"
version = "1.0.0-SNAPSHOT"

if (System.getProperty("quarkus.native.enabled") == "true" &&
    System.getProperty("quarkus.package.jar.enabled") == null
) {
    System.setProperty("quarkus.package.jar.enabled", "false")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        javaParameters = true
    }
}

val generateVersionProperties by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/resources/version")
    outputs.dir(outputDir)
    outputs.upToDateWhen { false }
    doLast {
        val gitHash =
            runCatching {
                val process = ProcessBuilder("git", "rev-parse", "--short=7", "HEAD").start()
                val text =
                    process.inputStream
                        .bufferedReader()
                        .readText()
                        .trim()
                if (process.waitFor() == 0 && text.length == 7) text else null
            }.getOrNull()
                ?: System
                    .getenv("GIT_COMMIT")
                    ?.trim()
                    ?.takeIf { it.length >= 7 }
                    ?.take(7)
                ?: "unknown"

        val versionFile = outputDir.get().file("version.properties").asFile
        versionFile.parentFile.mkdirs()
        versionFile.writeText("version=${project.version}\ncommit=${gitHash}\n")
    }
}

val extractLeafletAssets by tasks.registering {
    val runtimeClasspath = configurations.named("runtimeClasspath")
    inputs.files(runtimeClasspath)
    val outputDir = layout.buildDirectory.dir("generated/resources/leaflet")
    outputs.dir(outputDir)
    doLast {
        val leafletJar =
            runtimeClasspath
                .get()
                .files
                .firstOrNull { it.name.startsWith("leaflet-") && it.name.endsWith(".jar") }
                ?: throw GradleException("org.mvnpm:leaflet JAR not found on runtimeClasspath")
        val targetVendorDir = outputDir.get().dir("META-INF/resources/vendor/leaflet").asFile
        targetVendorDir.deleteRecursively()
        targetVendorDir.mkdirs()
        ZipFile(leafletJar).use { zip ->
            val distPrefix =
                zip
                    .entries()
                    .asSequence()
                    .map { it.name }
                    .firstOrNull {
                        it.startsWith("META-INF/resources/_static/leaflet/") && it.contains("/dist/")
                    }?.substringBefore("/dist/")
                    ?.plus("/dist/")
                    ?: throw GradleException("Could not find dist/ folder inside ${leafletJar.name}")
            zip.entries().asSequence().forEach { entry ->
                if (!entry.isDirectory && entry.name.startsWith(distPrefix)) {
                    val relPath = entry.name.removePrefix(distPrefix)
                    if (relPath.isNotEmpty()) {
                        val outFile = File(targetVendorDir, relPath)
                        outFile.parentFile.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            outFile.outputStream().use { output -> input.copyTo(output) }
                        }
                    }
                }
            }
        }
    }
}

sourceSets.main {
    resources.srcDir(generateVersionProperties)
    resources.srcDir(extractLeafletAssets)
}

tasks.named("processResources") {
    dependsOn(generateVersionProperties, extractLeafletAssets)
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint().editorConfigOverride(mapOf("max_line_length" to "120"))
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint().editorConfigOverride(mapOf("max_line_length" to "120"))
    }
    format("web") {
        target("src/main/resources/META-INF/resources/**/*.{html,css,js}")
        targetExclude("src/main/resources/META-INF/resources/vendor/**")
        trimTrailingWhitespace()
        leadingTabsToSpaces(2)
        endWithNewline()
    }
}

val lintJs by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Lints frontend JavaScript files using Google Closure Compiler on pure JVM"
    classpath = closureCompiler
    mainClass.set("com.google.javascript.jscomp.CommandLineRunner")

    val jsFiles = fileTree("src/main/resources/META-INF/resources/js") { include("*.js") }
    inputs.files(jsFiles)
    val markerFile = layout.buildDirectory.file("reports/lintJs/marker.txt")
    outputs.file(markerFile)

    doFirst {
        val sortedFiles = jsFiles.files.sortedBy { it.name }
        if (sortedFiles.isEmpty()) {
            throw GradleException("No JavaScript files found to lint in src/main/resources/META-INF/resources/js")
        }
        args = listOf(
            "--compilation_level=WHITESPACE_ONLY",
            "--checks_only",
            "--warning_level=DEFAULT",
            "--language_in=ECMASCRIPT_NEXT",
            "--language_out=ECMASCRIPT_NEXT",
            "--jscomp_off=strictMissingProperties",
            "--jscomp_off=missingProvide",
            "--jscomp_off=missingRequire",
        ) + sortedFiles.map { "--js=${it.absolutePath}" }
    }

    doLast {
        markerFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText("PASSED\n")
        }
    }
}

tasks.named("check") {
    dependsOn(lintJs)
}
