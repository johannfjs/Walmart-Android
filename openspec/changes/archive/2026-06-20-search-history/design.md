## Context

To enhance search ergonomics, we need to persist previous search queries locally. We will implement this feature across the `:domain`, `:data`, and `:app` modules using the established Clean Architecture boundaries and a strict Test-Driven Development (TDD) cycle.

## Goals / Non-Goals

**Goals:**
- Create `search_history` Room table with a unique index on query text and a timestamp field.
- Implement `@Insert(onConflict = OnConflictStrategy.REPLACE)` in the DAO to auto-update existing queries and keep history sorted.
- Limit query results to the 10 most recent entries directly via the SQL query.
- Create use cases in `:domain` to validate/trim queries and fetch the history flow.
- Build view model state tracking (`SearchHistoryState`) and Jetpack Compose list views.
- Write tests first: use JVM unit tests for `:domain` and `:app`, and in-memory instrumented tests for `:data`'s DAO.

**Non-Goals:**
- Implement remote cloud search history synchronization.
- Implement search history deletion (clear button or swipe-to-delete).
- Design a search results details screen.

## Decisions

### 1. Database Indexing & De-duplication Strategy
- **Decision:** Add a unique index constraint on the `query_text` column of the Room entity. Apply `OnConflictStrategy.REPLACE` on the insert operation in `SearchHistoryDao`.
- **Rationale:** Offloads de-duplication to the SQLite engine. If a duplicate search query is entered, Room replaces the existing row. This updates the entry's primary key and timestamp to the latest values, naturally moving the query to the top of the history list without requiring manual lookup or deletion checks in code.

### 2. History Retrieval Cap (Top 10)
- **Decision:** Limit the return count directly in the SQLite query using `LIMIT 10` alongside `ORDER BY timestamp DESC`.
- **Rationale:** Ensures that database reads are highly performant and only fetch the maximum number of items required by the UI. Memory consumption remains constant, regardless of how many queries have been saved over time.

### 3. In-Memory Database for Integration Tests
- **Decision:** Implement `:data` tests in `androidTest` using Room's `inMemoryDatabaseBuilder`.
- **Rationale:** Guarantees that testing writes and read caps do not persist onto the device's storage, ensuring test hermeticity and preventing test pollution between runs.

## Risks / Trade-offs

### [Risk] Main thread blocking during database writes or reads
- **Description:** Accessing Room on the main thread throws runtime exceptions or causes frame drops.
- **Mitigation:** Define Room query returns as Kotlin `Flow`. Room automatically executes flow queries off the main thread. Run write operations (`saveSearchQuery`) inside `viewModelScope.launch(Dispatchers.IO)`.
