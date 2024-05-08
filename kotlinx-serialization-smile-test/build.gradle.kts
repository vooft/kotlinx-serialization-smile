plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotest.multiplatform)
}

kotlin {
    jvm()

    applyDefaultHierarchyTemplate()

    sourceSets {
        jvmTest.dependencies {
            implementation(project(":kotlinx-serialization-smile-core"))
            implementation(libs.jackson.dataformat.smile)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotest.framework.datatest)
        }
    }
}

