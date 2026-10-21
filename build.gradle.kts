plugins {
    java
    `maven-publish`
}

group = "dev.lu15"
version = "0.3.0-SNAPSHOT"

var minestomCommit = "2026.08.28-26.2"

// -----------------------------------------------------------------------
// Repositories
// -----------------------------------------------------------------------

repositories {
    mavenCentral()

    maven("https://repo.hypera.dev/snapshots/")
    maven("https://jitpack.io")

}

// -----------------------------------------------------------------------
// Dependencies
// -----------------------------------------------------------------------

dependencies {
    // --- Minestom ---
    // Matches the lobby in WorldOfKingdoms (gradle/libs.versions.toml).
    // Check https://github.com/Minestom/Minestom/releases for the newest.
    compileOnly("net.minestom:minestom:$minestomCommit")
    // Maven Central Minestom only has slf4j at runtime; the jitpack build exposed it.
    compileOnly("org.slf4j:slf4j-api:2.0.17")

    // --- Opus codec (concentus – pure-Java, no native lib required) ---
    // Used by the CLIENT-SIDE mod; the server only re-routes raw bytes,
    // so this is included here only if you want server-side decoding.
    // Remove if you don't need it.
    implementation("io.github.jaredmdobson:concentus:1.0.2")

    // --- Testing ---
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// -----------------------------------------------------------------------
// Java toolchain
// -----------------------------------------------------------------------

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
    withJavadocJar()
}

// -----------------------------------------------------------------------
// Tests
// -----------------------------------------------------------------------

tasks.test {
    useJUnitPlatform()
}

// Local-only runnable test server (gitignored). See testserver.gradle.kts.
if (file("testserver.gradle.kts").exists()) apply(from = "testserver.gradle.kts")

// -----------------------------------------------------------------------
// Publishing (Hypera snapshots repo, matching the original setup)
// -----------------------------------------------------------------------

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name = "simple-voice-chat-minestom"
                description = "Minestom port of Simple Voice Chat"
                url = "https://github.com/LooFifteen/simple-voice-chat-minestom"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
            }
        }
    }
    repositories {
        maven {
            val releasesUrl = "https://repo.hypera.dev/releases/"
            val snapshotsUrl = "https://repo.hypera.dev/snapshots/"
            url = uri(if (version.toString().endsWith("-SNAPSHOT")) snapshotsUrl else releasesUrl)
            credentials {
                username = System.getenv("HYPERA_USERNAME")
                password = System.getenv("HYPERA_TOKEN")
            }
        }
    }
}
