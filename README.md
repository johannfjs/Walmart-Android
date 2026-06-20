# Walmart - eCommerce Product Search Engine

This project is a native Android eCommerce mobile application designed to allow users to search for products from a catalog through agile, paginated remote network service integration.

The development has been carefully structured following the principles of **Clean Architecture**, **Spec-Driven Development (SDD)** via Open Spec, and Test-Driven Development (**TDD**).

---

## 🛠️ Tech Stack & Architecture

### Multi-Module Architecture
The application is split into 4 independent modules adhering to Clean Architecture principles to ensure decoupling, ease of unit testing, and system scalability:
- **`:domain`**: Contains pure business logic rules (Domain Models, Repository Interfaces, and Use Cases). Completely free from Android framework dependencies.
- **`:data`**: Implements local data persistence and remote network service calls (Room DB, Retrofit, Mappers, and Repository Implementations).
- **`:di`**: A centralized module responsible for dependency provision and injection using Hilt.
- **`:app`**: The presentation layer managing the UI with Jetpack Compose and view state logic using the **MVVM** architectural pattern.

### Core Libraries
- **UI:** Jetpack Compose (Reactive and declarative UI design).
- **Dependency Injection:** Hilt (Dagger-Hilt) for Android.
- **Network:** Retrofit 2 & OkHttp3 for consuming the Walmart REST API.
- **Local Persistence:** Room Database for caching product data and preserving search history.
- **Image Processing:** Coil (Asynchronous and efficient thumbnail image loading).
- **Asynchrony:** Kotlin Coroutines & Flow (Reactive, non-blocking data streams).
- **Testing:** JUnit 4, Mockito, kotlinx-coroutines-test, and Compose Test Rule for instrumentation.

---

## 📋 Challenge Requirements

The application strictly covers the following functional criteria established by the GAPSI evaluation:

- **Search by Text:** Allows users to perform effective product searches based on input text.
- **External API Consumption:** Integration with the keyword search endpoint provided through the RapidAPI platform.
- **Detailed List:** Dynamic display of results asynchronously showing the title, price, and image (thumbnail) for each product.
- **Search History:** Local persistence of queries previously searched by the user. This history remains intact and available even after a full application restart.
- **Incremental Loading (Pagination):** Dynamic fetching of consecutive pages as the user scrolls vertically down the results list.
- **Fluid User Experience:** Asynchronous processing delegated to background threads to guarantee a friendly interface that never blocks during network queries.
- **Compatibility:** Natively built to run on devices with Android 12 (API Level 31) and above.

---

## 🔍 Integrated API Service Details

The data layer communicates non-blockingly with the following endpoint:
`GET https://axesso-walmart-data-service.p.rapidapi.com/wlm/walmart-search-by-keyword`

### Query Parameters
- `keyword`: The search criteria entered by the user. It is highly recommended to test using English words or well-known tech brands like "nintendo" or "sony".
- `page`: The sequential page index number to request for the infinite scroll.
- `sortBy`: Sorting criteria (Set to `"best_match"` by default).

### Required Headers
For every network request, the following authentication header is automatically injected:
- `x-rapidapi-key`: `[YOUR_API_KEY_HERE]`

---

## 🧪 Testing Strategy (Quality Assurance)

- **Unit Tests (TDD):** Business logic coverage within Use Cases and validation of state emissions in ViewModels using Mockito to isolate data components.
- **Local Cache Tests:** Controlled in-memory database tests on Room DAOs to ensure the consistency of both search history entries and cached products.
- **UI & Instrumentation Tests:** Automated testing with `ComposeTestRule` to validate end-to-end user flows: simulating text input, clicking the search action, vertical scrolling, and navigating to the product detail screen.

---

## 🚀 Compilation & Execution Instructions

### Prerequisites
1. Android Studio Ladybug (or higher).
2. Android SDK 31 (Android 12) installed.
3. Gradle configured with up-to-date Java/Kotlin toolchain support.

### API Key Configuration
For the app to successfully communicate with the web services, open the `local.properties` file in the root directory of your project and add your shared key exactly like this:

```properties
RAPIDAPI_KEY="your_api_key_here"
```