plugins {
    id("plumb.intellij-module")
    id("org.jetbrains.intellij.platform.module")
}

dependencies {
    api(project(":metrics-model"))

    intellijPlatform {
        intellijIdea(providers.gradleProperty("platformVersion"))
        bundledPlugin("com.intellij.java")
    }
}
