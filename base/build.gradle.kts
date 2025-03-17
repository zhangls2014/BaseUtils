plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

// 读取 git 的提交数量作为版本号
val gitVersionCode by lazy {
    val stdout = org.apache.commons.io.output.ByteArrayOutputStream()
    rootProject.exec {
        val cmd = "git rev-list HEAD --first-parent --count".split(" ")
        commandLine(cmd)
        standardOutput = stdout
    }
    stdout.toString(Charsets.UTF_8).trim().toInt()
}

// 读取 git 的 commit tag 作为应用的版本名，如果后面
val gitVersionTag by lazy {
    val stdout = org.apache.commons.io.output.ByteArrayOutputStream()
    rootProject.exec {
        val cmd = "git describe --tags".split(" ")
        commandLine(cmd)
        standardOutput = stdout
    }
    stdout.toString(Charsets.UTF_8).trim()
}

android {
    namespace = "com.zhangls.base"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTool.get()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        consumerProguardFiles("proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    lint {
        disable.add("SpUsage")
        disable.add("RtlHardcoded")
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        jniLibs {
            pickFirsts.add("lib/arm64-v8a/libc++_shared.so")
            pickFirsts.add("lib/armeabi-v7a/libc++_shared.so")
            excludes.add("**/*.proto")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // 测试、Debug
    testImplementation(libs.junit)
    androidTestImplementation(libs.extJunit)
    androidTestImplementation(libs.espresso)

    api(libs.coroutinesAndroid)
    api(libs.coreKtx)
    api(libs.materialDesign)
    api(libs.constraintLayout)
    api(libs.lifecycleRuntime)
    api(libs.activity)
    api(libs.navigationFragmentKtx)
    api(libs.datastorePreferences)

    // 网络请求框架：Retrofit + Gson
    api(libs.gson)
    api(libs.retrofit2)
    api(libs.retrofit2ConverterGson)
    api(libs.okHttpLogging)
    api(libs.utilcodex)
}

afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components["release"])

                groupId = "com.zhangls"
                artifactId = "base"
                version = gitVersionTag
            }
        }
    }
}