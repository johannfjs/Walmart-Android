## 1. Domain Layer (TDD)

- [x] 1.1 Create the pure Kotlin data class `Product` inside the `:domain` module
- [x] 1.2 Define the `ProductRepository` interface inside the `:domain` module
- [x] 1.3 Write `SearchProductsUseCaseTest` and `FetchNextPageUseCaseTest` unit tests under `:domain` verifying the stream emission and incremental page requests
- [x] 1.4 Implement `SearchProductsUseCase` and `FetchNextPageUseCase` inside the `:domain` module

## 2. Data Layer Setup (TDD)

- [x] 2.1 Write `ProductCacheDaoTest` integration tests in `:data/src/androidTest` to verify insertion, sorting order, and cache eviction
- [x] 2.2 Create `ProductCacheEntity` Room database entity inside `:data`
- [x] 2.3 Create `ProductCacheDao` interface with `@Insert(onConflict = OnConflictStrategy.REPLACE)` and select/eviction queries
- [x] 2.4 Register `ProductCacheEntity` and `ProductCacheDao` in `AppDatabase`
- [x] 2.5 Define the Retrofit API service interface for Walmart search by keyword endpoint
- [x] 2.6 Implement `ProductRepositoryImpl` using Room and Retrofit, handling non-blocking IO operations
- [x] 2.7 Write integration tests for `ProductRepositoryImpl` to verify network fetch and local persistence integration

## 3. Dependency Injection Integration & Cleanup

- [x] 3.1 Expose the Retrofit service provider and DAO provider bindings in `:di`'s `DatabaseModule` and `NetworkModule`
- [x] 3.2 Bind `ProductRepository` to `ProductRepositoryImpl` in `:di`'s `RepositoryModule`
- [x] 3.3 Delete the deprecated dummy classes (`DummyDao`, `DummyEntity`, `DefaultDataRepository`, `DataRepository`) and remove their Hilt bindings from `:di` modules
- [x] 3.4 Grant the INTERNET permission in the app's `AndroidManifest.xml` file
- [x] 3.5 Retrieve the RAPIDAPI_KEY from `local.properties` and expose it via `:di`'s `BuildConfig`

## 4. Presentation Layer (TDD)

- [x] 4.1 Write `ProductSearchViewModelTest` under `:app/src/test` to verify UI state transitions and pagination locking rules
- [x] 4.2 Define the `ProductSearchUiState` sealed interface inside the `:app` module
- [x] 4.3 Implement `ProductSearchViewModel` inside `:app` invoking the domain use cases and managing the page count and lock state
- [x] 4.4 Create Jetpack Compose components `ProductLazyList` and `ProductListItem` inside `:app` using Coil for image loading
- [x] 4.5 Connect the product search and infinite scroll presentation inside `MainScreen` Compose layout

## 5. Verification & Acceptance Criteria

- [x] 5.1 Execute `./gradlew assembleDebug` to compile the multi-module project
- [x] 5.2 Run `./gradlew test` to execute all unit tests across all modules
- [x] 5.3 Audit `:domain` dependencies to confirm zero Android imports exist
