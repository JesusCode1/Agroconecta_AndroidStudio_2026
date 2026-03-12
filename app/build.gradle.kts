plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.cibertec.agroconecta"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cibertec.agroconecta"
        minSdk = 23
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebaseAuth)  /*Este  lib es para Autenticaion con firebase*/
    implementation(libs.firebaseDatabase)  /*Base de datos Firebase*/

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lottie)  /*Este  lib es para animaciones*/
    implementation(libs.imagePicker)/*recorta img*/
    implementation(libs.glide)/*leer img*/
    implementation(libs.storage)/*subir archivosmultimedia*/
    implementation(libs.maps)
    implementation(libs.places)
    implementation(libs.circleImage)
    implementation(libs.authGoogle)
    implementation(libs.ctp) /*codigo de telefono por pais*/


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}