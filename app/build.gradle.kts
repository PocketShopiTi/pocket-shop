import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.secrets)
    alias(libs.plugins.apollo)
}


val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

apollo {
    service("shopify") {
        packageName.set("com.iti.pocketshop.shopify")
        srcDir("src/main/graphql/client")

        introspection {
            endpointUrl.set("https://mad46-and4.myshopify.com/api/2026-04/graphql.json")
            headers.put(
                "X-Shopify-Storefront-Access-Token",
                localProperties["STORE_FRONT_TOKEN"].toString()
            )
            schemaFile.set(file("src/main/graphql/client/schema.graphqls"))
        }

        mapScalar("Decimal", "kotlin.Double")
        mapScalar("URL", "kotlin.String")
        mapScalar("DateTime", "kotlin.String")
        mapScalar("UnsignedInt64", "kotlin.Long")
    }

    service("shopifyAdmin") {
        packageName.set("com.iti.pocketshop.shopify.admin")
        srcDir("src/main/graphql/admin")

        introspection {
            endpointUrl.set("https://mad46-and4.myshopify.com/admin/api/2026-04/graphql.json")
            headers.put(
                "X-Shopify-Access-Token",
                localProperties["SHOPIFY_ADMIN_TOKEN"].toString()
            )
            schemaFile.set(file("src/main/graphql/admin/schema.graphqls"))
        }
        mapScalar("Decimal", "kotlin.Double")
        mapScalar("URL", "kotlin.String")
        mapScalar("DateTime", "kotlin.String")
        mapScalar("UnsignedInt64", "kotlin.Long")
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

        val mapsApiKey = localProperties["MAPS_API_KEY"]
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey as? String ?: ""

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
        dataBinding = true
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
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //todo: remove these
    implementation(libs.androidx.compose.material.icons.extended)

    // collect as state with lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)

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

    // google maps
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)

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
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // firebase auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)

    // Credential Manager for Google Sign-In
    //noinspection LoginCredentials
    implementation(libs.androidx.credentials)
    //noinspection LoginCredentials
    implementation(libs.androidx.credentials.play.services.auth)
    //noinspection LoginCredentials
    implementation(libs.googleid)
    implementation(libs.osmdroid.android)

    // paymob
    implementation(libs.paymob.sdk)
}
