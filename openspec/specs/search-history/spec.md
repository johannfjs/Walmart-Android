# Search History Spec

## Purpose
This specification outlines the requirements for saving, validating, de-duplicating, and presenting search queries to users.

## Requirements

### Requirement: Search History Retrieval and Stream
The system SHALL retrieve the list of previously saved search queries as a stream (`Flow`). The returned list MUST contain unique search terms, sorted descending by their timestamp, and capped at a maximum of 10 items.

#### Scenario: Verify history stream order and cap limit
- **WHEN** 12 distinct search queries are saved in the database
- **THEN** the repository stream SHALL emit only the 10 most recently searched queries, sorted descending by timestamp.

---

### Requirement: Search Query Input Validation
The system SHALL validate any query prior to persistence. It MUST ignore empty or blank inputs and trim leading or trailing whitespaces from valid queries.

#### Scenario: Save empty or blank query
- **WHEN** a search is triggered with a blank or empty string (e.g., "   ")
- **THEN** the query validation fails and the repository save operation is NOT invoked.

#### Scenario: Save valid query with whitespace
- **WHEN** a search is triggered with a valid string containing leading/trailing whitespace (e.g., "  PlayStation 5  ")
- **THEN** the query is trimmed to "PlayStation 5" and passed to the repository for persistence.

---

### Requirement: De-duplication of Queries
The database SHALL enforce a uniqueness constraint on query text. Saving a query that already exists MUST update its timestamp to the current time (moving it to the top of the list) without creating duplicate entries.

#### Scenario: Search for an existing term
- **WHEN** a query text "nintendo" is saved for the second time
- **THEN** the timestamp is updated to the latest search time and "nintendo" appears exactly once at the top of the history list.

---

### Requirement: Presentation Layer History Interaction
The presentation layer SHALL render the search history list when the search field is focused or empty. Tapping a history item MUST automatically populate the search text input and execute the search action.

#### Scenario: Tap history item
- **WHEN** a user clicks a history item displaying the query "zelda"
- **THEN** the current search query text state updates to "zelda" and a new search event is executed.
