import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * Copyright 2017 Alicia Boya García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  kotlin("jvm") version "1.6.10"

  id("me.qoomon.git-versioning") version "5.1.2"

  jacoco
}

project.group = "me.ntrrgc"
project.version = "0.0.0-SNAPSHOT"
gitVersioning.apply {
  refs {
    branch(".+") { version = "\${ref}-SNAPSHOT" }
    tag("v(?<version>.*)") { version = "\${ref.version}" }
  }

  // optional fallback configuration in case of no matching ref configuration
  rev { version = "\${commit}" }
}

val spekVersion = "1.1.5"
val junitVersion = "5.8.2"
val expektVersion = "0.5.0"
val googleFindBugsVersion = "3.0.2"

dependencies {
  implementation(kotlin("reflect"))

  testImplementation(platform("org.junit:junit-bom:$junitVersion"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
    because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
  }

  testImplementation("com.winterbe:expekt:$expektVersion")
  testImplementation("org.jetbrains.spek:spek-api:$spekVersion")
  testImplementation("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")

  testImplementation("com.google.code.findbugs:jsr305:$googleFindBugsVersion")
}

java {
  withSourcesJar()
}

kotlin {
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "1.8"
    apiVersion = "1.6"
    languageVersion = "1.6"
  }
}

tasks.withType<Test> {
  useJUnitPlatform {
    includeEngines("spek")
  }
}
tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}

tasks.wrapper {
  gradleVersion = "7.3.3"
  distributionType = Wrapper.DistributionType.ALL
}
