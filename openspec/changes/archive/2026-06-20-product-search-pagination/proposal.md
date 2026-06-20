## Why

Currently, the Walmart Android application lacks the capability to search for products remotely and navigate results efficiently using pagination. To establish a robust, performant product catalog experience, we need a network layer to fetch remote products, a local Room database cache to act as the Single Source of Truth (SSOT) to support offline access, Kotlin Coroutines/Flow to handle non-blocking page-by-page fetching, and an infinite scrolling UI.

## What Changes

- **New remote API configuration**: Set up Retrofit and OkHttpClient to query the Walmart search keyword API with automated headers.
- **Room Cache layer**: Design a local database cache (`products_cache`) with a schema that maps remote attributes and records cached page indices.
- **Clean Architecture interfaces & Usecases**: Expose domain repository contracts and use cases for listing cached streams and requesting new pages in a decoupled, testable manner.
- **Presentation State**: Implement pagination logic and UI states (Idle, Loading, Success, Error, Empty) in a lifecycle-aware ViewModel.
- **Compose UI components**: Build a paginated list using Jetpack Compose `LazyColumn` and Coil for image loading, equipped with an infinite scroll trigger.
- **Cleanup**: Remove initial boilerplate/dummy files (`DummyDao`, `DummyEntity`, `DefaultDataRepository`, `DataRepository`, and their associated DI bindings) if no longer required.

## Capabilities

### New Capabilities
- `product-search-pagination`: Fetches remote product results by keyword, persists them in a Room-based local cache, supports sequential non-blocking pagination, and renders them in an infinite scrolling user interface.

### Modified Capabilities
<!-- Existing capabilities whose REQUIREMENTS are changing (not just implementation).
     Only list here if spec-level behavior changes. Each needs a delta spec file.
     Use existing spec names from openspec/specs/. Leave empty if no requirement changes. -->

## Impact

- **Network Layer**: Requires network permissions and Hilt-configured Retrofit setup.
- **Database Layer**: Adds a new table `products_cache` in Room database.
- **UI & Presentation**: Refactors `MainScreen` to display the new paginated search view.
- **Boilerplate Cleanup**: Deprecates and deletes placeholder dummy classes and bindings.
