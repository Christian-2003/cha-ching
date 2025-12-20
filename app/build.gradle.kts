plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.ksp)
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.androidx.room)
	alias(libs.plugins.hilt)
	kotlin("plugin.serialization") version "2.1.21"
}

android {
	namespace = "de.christian2003.chaching"
	compileSdk = 35

	defaultConfig {
		applicationId = "de.christian2003.chaching"
		minSdk = 34
		targetSdk = 35
		versionCode = 8
		versionName = "1.2.1-pre1"
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

	room {
		schemaDirectory("$projectDir/schemas")
	}

	sourceSets {
		getByName("androidTest").assets.srcDir("$projectDir/schemas")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll(
			"-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
			"-opt-in=kotlin.uuid.ExperimentalUuidApi",
			"-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
			"-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
			"-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
		)
	}
}

android.applicationVariants.all {
	outputs.all {
		val appName = "cha-ching"
		val versionName = versionName
		(this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = "$appName-v$versionName.apk"
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
	implementation(libs.androidx.room)
	implementation(libs.androidx.room.runtime)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.compose.charts)
	implementation(libs.apkupdater.library)
	implementation(libs.androidx.core.splashscreen)
	implementation(libs.coil.compose)
	implementation(libs.coil.svg)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.androidx.glance.appwidget)
	implementation(libs.androidx.glance.material3)
	implementation(libs.androidx.animation.graphics)
	implementation(libs.okhttp)
	implementation(libs.androidx.hilt.navigation.compose)
	implementation(libs.hilt.android)
	implementation("com.ibm.icu:icu4j:78.1")

	testImplementation(libs.junit)
	testImplementation(libs.mockito.kotlin)

	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	androidTestImplementation(libs.androidx.ui.test)

	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)

	annotationProcessor(libs.androidx.room.compiler)

	ksp(libs.androidx.room.compiler)
	ksp(libs.hilt.compiler)
}
