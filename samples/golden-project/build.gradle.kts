plugins {
    java
}

group = "golden"
version = "1.0"

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
    options.encoding = "UTF-8"
}
