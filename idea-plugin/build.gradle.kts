import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("plumb.intellij-module")
    id("org.jetbrains.intellij.platform")
    alias(libs.plugins.changelog)
}

dependencies {
    implementation(project(":metrics-core"))
    testImplementation(libs.junit4)

    intellijPlatform {
        intellijIdea(providers.gradleProperty("platformVersion"))
        bundledPlugin("com.intellij.java")
        pluginComposedModule(implementation(project(":adapter-psi")))
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    projectName = rootProject.name
}

changelog {
    path = rootProject.file("CHANGELOG.md").path
}
