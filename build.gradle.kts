plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Selenium WebDriver 의존성
    implementation("org.seleniumhq.selenium:selenium-java:4.1.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.1.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.4.1")
    implementation("org.apache.commons:commons-pool2:2.11.1")
    implementation("org.apache.pdfbox:pdfbox:3.0.0")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("org.apache.pdfbox:pdfbox:2.0.24")

}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar  {
    archiveBaseName.set("autoPDF")
    archiveVersion.set("")
    archiveExtension.set("jar")
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}