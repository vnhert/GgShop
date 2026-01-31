plugins {

    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)

}



android {

    namespace = "com.example.ggshop"

    compileSdk {

        version = release(36)

    }



    defaultConfig {

        applicationId = "com.example.ggshop"

        minSdk = 24

        targetSdk = 36

        versionCode = 1

        versionName = "1.0"



        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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

    composeOptions {

        kotlinCompilerExtensionVersion = "1.5.1" // Verifica que esta versión sea compatible con tu Kotlin

    }

}



dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.ui.graphics)

    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(platform(libs.androidx.compose.bom))

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)

    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.core:core-ktx:1.12.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    implementation("androidx.activity:activity-compose:1.8.2")



// --- COMPOSE UI (Aquí estaban tus errores) ---

    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")

    implementation(composeBom)

    implementation("androidx.compose.ui:ui")

    implementation("androidx.compose.ui:ui-graphics")

    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3:1.2.0")



// --- NAVEGACIÓN (Lo que necesita tu MainActivity) ---

    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")



// --- IMÁGENES Y RED (Para los productos de GgShop) ---

    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("io.coil-kt:coil-compose:2.6.0")



// Herramientas de debug

    debugImplementation("androidx.compose.ui:ui-tooling")

    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("org.osmdroid:osmdroid-android:6.1.20")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    implementation("androidx.navigation:navigation-compose:2.7.7")



// Corrutinas y DataStore (Persistencia)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("androidx.datastore:datastore-preferences:1.1.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("io.coil-kt:coil-compose:2.7.0")



// Testing Básico y Coroutines

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")



// Kotest & MockK

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")

    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    testImplementation("io.mockk:mockk:1.13.8")



// UI Testing (Compose)

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-test-manifest")



//Room

    implementation(libs.androidx.room.runtime)

    implementation(libs.androidx.room.ktx)

    ksp(libs.androidx.room.compiler)



}