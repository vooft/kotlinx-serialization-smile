plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.allopen)
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("kotlinx.benchmark.State")
}

kotlin {
    jvm()
    macosArm64()

    js(IR) {
        browser()
        nodejs()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(project(":kotlinx-serialization-smile-core"))
            implementation(project(":kotlinx-serialization-smile-test"))
        }

        jvmMain.dependencies {
            implementation(libs.jackson.dataformat.smile)
            implementation(libs.jackson.module.kotlin)
            implementation(libs.kotlin.reflect)
            implementation(libs.bundles.logging.jvm)
        }

        jsMain.dependencies {
            implementation(libs.kotlin.logging.js)
        }

        nativeMain { }
    }
}

benchmark {
    targets.register("jvm")
    targets.register("js")

    // not working for some reason
    targets.register("macosArm64")
}
