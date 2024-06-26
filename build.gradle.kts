plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.serialization)
    id("maven-publish")
}

group = "win.snowma"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.serialization)
    implementation(libs.kotlin.reflect)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["kotlin"])
                groupId = "win.snowma"
                artifactId = "i18nKt"
                version = "1.0"
            }
        }
    }
}