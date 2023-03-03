/*
 * Copyright (c) 2023.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    // https://plugins.gradle.org/plugin/org.springframework.boot
    id("org.springframework.boot") version "3.0.3" apply false
    // https://plugins.gradle.org/plugin/io.spring.dependency-management
    id("io.spring.dependency-management") version "1.1.0" apply false
    // https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html
    val kotlinVersion: String by System.getProperties() // see gradle.properties
    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
}

subprojects {
    group = "dev.mbo"
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    val kotlinVersion: String by System.getProperties()
    apply(plugin = "io.spring.dependency-management")
    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        resolutionStrategy {
            cacheChangingModulesFor(
                0,
                "seconds"
            )
        }
        imports {
            // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-parent
            mavenBom("org.springframework.cloud:spring-cloud-starter-parent:2022.0.1")
            // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.1")
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)

            // https://mvnrepository.com/artifact/org.testcontainers/testcontainers-bom
            mavenBom("org.testcontainers:testcontainers-bom:1.17.6")
            // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-bom
            mavenBom("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion")
        }
        dependencies {
            // https://mvnrepository.com/artifact/org.mapstruct/mapstruct
            dependency("org.mapstruct:mapstruct:1.5.3.Final")
            // https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor
            dependency("org.mapstruct:mapstruct-processor:1.5.3.Final")

            // https://mvnrepository.com/artifact/org.hibernate/hibernate-jpamodelgen
            dependency("org.hibernate:hibernate-jpamodelgen:5.6.15.Final")
            // https://mvnrepository.com/artifact/io.rest-assured/rest-assured
            dependency("io.rest-assured:rest-assured:5.3.0")
            // https://mvnrepository.com/artifact/io.swagger.core.v3/swagger-annotations
            dependency("io.swagger.core.v3:swagger-annotations:2.2.8")
            // https://mvnrepository.com/artifact/org.keycloak/keycloak-admin-client
            dependency("org.keycloak:keycloak-admin-client:21.0.0")
            // https://mvnrepository.com/artifact/com.tngtech.archunit/archunit
            dependency("com.tngtech.archunit:archunit:1.0.1")
        }
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "17"
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }

        withType<Copy> {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}

tasks {
    withType<Wrapper> {
        // https://gradle.org/releases/
        gradleVersion = "8.0.1"
        distributionType = Wrapper.DistributionType.BIN
    }
}