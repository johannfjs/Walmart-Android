## 1. Project Root & Gradle Configuration

- [ ] 1.1 Bootstrap the project by executing the Android CLI command `android create empty-activity --name="Walmart" --minSdk=31 --output=.`
- [ ] 1.2 Structure/rename the generated directories to define modules `:app`, and create empty directories for `:domain`, `:data`, and `:di`
- [ ] 1.3 Update the root-level `settings.gradle.kts` to register all four modules (`:app`, `:domain`, `:data`, `:di`)
- [ ] 1.4 Create or update `gradle/libs.versions.toml` catalog specifying versions for Hilt, Room, Retrofit, OkHttp3, Coroutines, Coil, Compose, and testing libraries
- [ ] 1.5 Configure the root-level `build.gradle.kts` with KSP, Hilt, and Android plugins

## 2. :domain Module Setup

- [ ] 2.1 Create `:domain/build.gradle.kts` as a pure Kotlin module (no Android plugins or dependencies)
- [ ] 2.2 Initialize placeholder directories inside `:domain` for `model`, `repository`, and `usecase` packages

## 3. :data Module Setup

- [ ] 3.1 Create `:data/build.gradle.kts` as an Android library and configure Room, Retrofit, KSP, and Coroutines dependencies
- [ ] 3.2 Create database configuration placeholders (e.g. Room Database class stub)
- [ ] 3.3 Create Retrofit network client configuration placeholders (e.g. OkHttpClient provider stub)
- [ ] 3.4 Initialize placeholder directories inside `:data` for `datasource` (local/remote), `entity`, `dto`, and `repository` packages

## 4. :di Module Setup

- [ ] 4.1 Create `:di/build.gradle.kts` as an Android library with dependencies on `:domain` and `:data` modules
- [ ] 4.2 Initialize Hilt Module placeholder classes: `NetworkModule`, `DatabaseModule`, and `RepositoryModule`

## 5. :app Module Setup

- [ ] 5.1 Create `:app/build.gradle.kts` as an Android Application, adding dependency mappings to `:domain` and `:di` (no dependency on `:data`)
- [ ] 5.2 Create the `com.johannjara.walmart.WalmartApplication` class extending `android.app.Application` and annotated with `@HiltAndroidApp`
- [ ] 5.3 Configure `:app/src/main/AndroidManifest.xml` registering `WalmartApplication` in the `<application>` tag
- [ ] 5.4 Generate boilerplate Compose files for Theme, Colors, and Typography
- [ ] 5.5 Initialize placeholder directories inside `:app` for `ui`, `theme`, and `viewmodel` packages

## 6. Verification and Integration

- [ ] 6.1 Execute `./gradlew assembleDebug` to compile the multi-module project
- [ ] 6.2 Audit `:domain` dependencies to confirm zero Android imports exist
- [ ] 6.3 Run tests to verify the Hilt injection graph integrity
