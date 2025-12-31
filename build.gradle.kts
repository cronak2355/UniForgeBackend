plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// 1. Spring Boot Web (RestController 등을 위해 필수)
	implementation("org.springframework.boot:spring-boot-starter-web")

	// 2. AWS SDK (Unresolved reference 'aws' 해결)
	implementation("aws.sdk.kotlin:bedrockruntime:1.0.0")
	implementation("aws.sdk.kotlin:translate:1.0.0")

	// 3. Kotlin Coroutines (runBlocking 해결)
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

	// 4. JSON 라이브러리 (JSONObject 해결)
	implementation("org.json:json:20231013")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}