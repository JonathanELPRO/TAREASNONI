import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.process.CommandLineArgumentProvider
import com.google.protobuf.gradle.id



class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
        //lo de arriba retorna un una lista de argumentos osea una lista de strings
        //un argumento para room tiene el siguiente formato room.schemaLocation=${schemaDir.path}
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("io.sentry.android.gradle") version "5.9.0"
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.google.protobuf)


}

ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
    //ksp necesita recibir un procesador de argumentos el cual se lo pasa con arg
    //este procesador de argumentos retorna una lista de argumentos
    //donde solo nos importa el primero, este argumento lo agarra KSP y porteriormente se lo pasa a room
    //de esta forma room sabra donde guardar los esquemas
}

ktlint {
    android = true
    outputColorName = "RED"
    verbose = true
    ignoreFailures = true
    enableExperimentalRules = true
    baseline = file("$projectDir/config/ktlint/baseline.xml")
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/"))
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

android {
    namespace = "com.calyrsoft.ucbp1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.calyrsoft.ucbp1"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Si estuvieras usando KAPT/javac en lugar de KSP, se haría así:
        // javaCompileOptions {
        //     annotationProcessorOptions {
        //         compilerArgumentProviders(
        //             RoomSchemaArgProvider(File(projectDir, "schemas"))
        //         )
        //     }
        // }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    sourceSets {
        // ✅ Esto permite incluir los esquemas en los tests instrumentados
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.room.testing)

    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.bundles.local)

    // Room (2.8.3)
    implementation("androidx.room:room-runtime:2.8.3")
    ksp("androidx.room:room-compiler:2.8.3")
    implementation("androidx.room:room-ktx:2.8.3")
    testImplementation("androidx.room:room-testing:2.8.3")

    testImplementation(kotlin("test"))
    implementation(libs.datastore)
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.work.runtime.ktx)
    //proto
    implementation(libs.grpc.protobuf.lite)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.grpc.kotlin.stub)
    runtimeOnly(libs.grpc.okhttp)





}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        id("java") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${libs.versions.grpcKotlin.get()}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("java") { option("lite") }
                create("grpc") { option("lite") }
                create("grpckt") { option("lite") }
            }
            it.builtins {
                create("kotlin") { option("lite") }
            }
        }
    }
}


detekt {
    parallel = true
    ignoreFailures = true
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "11"
}


configurations.matching { it.name.startsWith("ksp") }.all {
    resolutionStrategy {
        force(
            // Update these versions
            "org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.0",
            "org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0"
        )
    }
}

//ksp tiene por detras su propio gradle y no necesariamente usara la version serialization que room necesita
//en este caso estamos forzando a una version serialization que room si usa la 1.8.0, porque ksp trabaja con room
//ya que ksp trabaja con las anotaciones como @Entity de room


