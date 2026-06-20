# Product Search Pagination Spec

## Purpose
This specification outlines the requirements for product search pagination, including remote product fetching, internet permission, local caching and eviction, non-blocking pagination logic, and UI state and infinite scroll interaction.

## Requirements

### Requirement: Remote Product Fetching
The repository SHALL consume the remote Walmart API keyword search service to request products by keyword and page number using a Retrofit service.

#### Scenario: Successful remote fetch
- **WHEN** a search is initiated for keyword "sony" at page 1 and the network service returns a success response with a list of products
- **THEN** the products are transformed to domain models and sent to the cache persistence layer.

---

### Requirement: Internet Permission
The application Android manifest MUST declare the `android.permission.INTERNET` permission to allow remote network calls.

#### Scenario: Verify internet permission in manifest
- **WHEN** the application is compiled
- **THEN** the manifest SHALL contain the `android.permission.INTERNET` permission.

---

### Requirement: Local Product Caching and Eviction
The local database SHALL act as the Single Source of Truth (SSOT). Remote fetched products MUST be inserted into the local cache (`products_cache`) with their associated search keyword and page index. The cache for a specific keyword MUST be evicted when a fresh new search query is initiated for that keyword.

#### Scenario: Insert and query cached products
- **WHEN** products for keyword "sony" on page 1 are cached and the database is queried
- **THEN** the query returns the list of cached products ordered by page index and ID.

#### Scenario: Evict cache on fresh search
- **WHEN** a fresh search query is triggered for a keyword that already has cached items in the database
- **THEN** the database deletes all cached products matching that keyword before storing new page 1 entries.

#### Scenario: Offline fallback from cache
- **WHEN** the network connection is lost and the repository is queried for a keyword
- **THEN** the system falls back to emitting the already cached items from the local database without throwing network exceptions.

---

### Requirement: Non-Blocking Pagination Logic
The presentation layer SHALL manage the current page state, handle incremental fetching sequentially, and prevent race conditions by ignoring duplicate fetch requests for the same page index that is already in progress.

#### Scenario: Prevent redundant API requests
- **WHEN** a pagination request is triggered for page 2 while a network query for page 2 is already active
- **THEN** the second request is skipped to prevent duplicate network calls.

---

### Requirement: UI State and Infinite Scroll Interaction
The UI SHALL display the paginated product search list, handle infinite scrolling by fetching the next page when the user scrolls near the bottom of the list (last 3 items), and transition through the states `Idle`, `Loading`, `Success`, `Error`, and `Empty`.

#### Scenario: Infinite scroll pagination trigger
- **WHEN** the user scrolls the product list and the last visible item index is within 3 items of the total item count
- **THEN** the ViewModel automatically requests the next page of products.

#### Scenario: Empty search results state
- **WHEN** a search returns zero product matches from both network and cache
- **THEN** the presentation state transitions to the `Empty` state.
