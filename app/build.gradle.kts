import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.secrets)
    alias(libs.plugins.apollo)
}


val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

apollo {
    service("shopify") {
        packageName.set("com.iti.pocketshop.shopify")

        introspection {
            endpointUrl.set("https://mad46-and4.myshopify.com/api/2026-04/graphql.json")
            headers.put(
                "X-Shopify-Storefront-Access-Token",
                localProperties["STORE_FRONT_TOKEN"].toString()
            )
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
        mapScalar(
            "Decimal",
            "kotlin.Double"
        )

        mapScalar(
            "URL",
            "kotlin.String"
        )
    }
}

android {
    namespace = "com.iti.pocketshop"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.iti.pocketshop"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.common)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //todo: remove these
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3.lint)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.tv.material)

    // collect as state with lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ViewModel scoping per NavEntry (Navigation 3)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // datastore proto
    implementation(libs.androidx.datastore)

    // room database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Kotlin json
    implementation(libs.kotlinx.serialization.json)

    // splash screen
    implementation(libs.androidx.core.splashscreen)

    // material3 expressive
    implementation(libs.androidx.material3.android)

    // coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    //work manager
    implementation(libs.androidx.work.runtime.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // hilt view model
    implementation(libs.androidx.hilt.navigation.compose)
    //hilt work
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    //lottie
    implementation(libs.lottie.compose)

    //kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // ktor
    implementation(libs.bundles.ktor)

    //Apollo
    implementation(libs.apollo.runtime)
    implementation(libs.logging.interceptor)

    // nav3
    implementation(libs.androidx.navigation3)
    implementation(libs.androidx.navigation3.ui)

    // firebase auth
    implementation(platform("com.google.firebase:firebase-bom:34.15.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")

    // Credential Manager for Google Sign-In
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
}