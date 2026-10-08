plugins {
    id("plumb.kotlin-base")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(kotlin("stdlib"))
}
