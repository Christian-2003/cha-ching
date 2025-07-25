plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
	alias(libs.plugins.compose.compiler)
	kotlin("plugin.serialization") version "2.1.21"
}

android {
	namespace = "de.christian2003.chaching"
	compileSdk = 35

	defaultConfig {
		applicationId = "de.christian2003.chaching"
		minSdk = 34
		targetSdk = 35
		versionCode = 5
		versionName = "1.1.2-pre1"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
		debug {
			applicationIdSuffix = ".debug"
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_19
		targetCompatibility = JavaVersion.VERSION_19
	}
	kotlinOptions {
		jvmTarget = "19"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.15"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
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
	implementation(libs.androidx.navigation.runtime.ktx)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.room)
	implementation(libs.room.runtime)
	implementation(libs.coroutines)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.compose.charts)
	implementation(libs.apkupdater.library)
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.coil.compose)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.androidx.glance.appwidget)
	implementation(libs.androidx.glance.material3)

	testImplementation(libs.junit)

	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	androidTestImplementation(libs.androidx.ui.test)
	androidTestImplementation(libs.ui.test.junit4)

	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)

	annotationProcessor(libs.room.compiler)

	ksp(libs.room.compiler)
}
