## Context

We are implementing a paginated product catalog search page. We need to configure a remote API connection using Retrofit/OkHttp, maintain a local cache (`products_cache`) as the Single Source of Truth (SSOT), and render a paginated list using Jetpack Compose with infinite scrolling (load-on-demand) triggered by the list state.

## Goals / Non-Goals

**Goals:**
- Implement Room database cache `products_cache` with keyword mapping and page index.
- Configure Retrofit client to query Walmart API with headers (`x-rapidapi-key`).
- Use Clean Architecture flow: UI -> Use Cases -> Repository interface -> Data repository/network/cache layers.
- Build ViewModel state machine with states (Idle, Loading, Success, Error, Empty).
- Support infinite scrolling by triggering page-2 (and so on) load when user scrolls near the bottom (last 3 items).
- Deprecate and clean up initial placeholder dummy classes and bindings (`DummyDao`, `DummyEntity`, `DefaultDataRepository`, `DataRepository`).

**Non-Goals:**
- Implement multiple sort options (hardcode `sortBy` to `"best_match"`).
- Implement local modification or deletion of cached products.
- Persist search query history (this was handled in a separate change).

## Decisions

### 1. Database-Backed SSOT Pagination
- **Decision:** Expose cached products stream via Room Flow: `getCachedProductsStream(keyword: String): Flow<List<Product>>`. Fetching new pages simply inserts them into Room, which automatically updates the flow.
- **Rationale:** Room acts as the Single Source of Truth. If the network call succeeds, database writes trigger a reactive UI update. If offline, the cache is read directly. This simplifies state management.

### 2. Header Injection Interceptor
- **Decision:** Configure OkHttpClient interceptor in Hilt's `NetworkModule` to dynamically inject `x-rapidapi-key` header on every request.
- **Rationale:** Prevents polluting individual API service calls with key definitions, standardizes security headers, and centralizes api-key management.

### 3. Duplicate Request Lock
- **Decision:** Maintain `isFetchingNextPage: Boolean` and `currentPage: Int` locks in the ViewModel to block trigger events while a network fetch is in progress for the current page counter.
- **Rationale:** Prevents redundant network calls when the scroll threshold is repeatedly hit before a network response arrives.

### 4. Boilerplate/Dummy File Cleanup
- **Decision:** Remove `DummyDao`, `DummyEntity`, `DefaultDataRepository`, `DataRepository` and their bindings in `DatabaseModule` and `RepositoryModule`.
- **Rationale:** Keeps the codebase clean, avoids compilation errors with unused references, and reduces bundle size.

## Risks / Trade-offs

### [Risk] Fast scroll triggers multiple API requests
- **Description:** User rapidly scrolls down, hitting the page-fetch boundary multiple times.
- **Mitigation:** The ViewModel locks additional fetches using a state-based guard until the current fetch completes.

### [Risk] Local database cache growth
- **Description:** Over time, the database grows as the user searches for many keywords.
- **Mitigation:** Evict (DELETE) cached results for the search keyword whenever a fresh search query is triggered (page 1 fetch).
