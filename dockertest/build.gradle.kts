plugins {
    id("buildsrc.convention.kotlin-jvm")
    application
}

dependencies {
    testImplementation(libs.bundles.junit.testing)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.kotest.junit5.jvm)
    implementation(libs.log4j.api)
    implementation(libs.log4j.core)
    testRuntimeOnly(libs.log4j.slf4j.impl)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

application {
    mainClass = "mf.vil.mf.vil.dockertest.MainKt"
}
