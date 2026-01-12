# <p align="center">
  <img src="docs/screenshots/banner.png" alt="Home Dark">
</p>

![Kotlin](https://img.shields.io/badge/kotlin-100%25-blue?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Android-Jetpack_Compose-green?style=for-the-badge&logo=android)
![Material Design 3](https://img.shields.io/badge/Design-Material_3-purple?style=for-the-badge&logo=materialdesign)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**BCN Transit** is a modern native Android application designed to facilitate mobility in Barcelona. It offers real-time information, interactive maps, and route planning for the city's entire public transport network.

<p align="center">
  <img src="docs/screenshots/01.jpg" width="24%" alt="Map">
  <img src="docs/screenshots/06.jpg" width="24%" alt="Metro Station">
  <img src="docs/screenshots/07.jpg" width="24%" alt="Bus Stop">
  <img src="docs/screenshots/08.jpg" width="24%" alt="Rodalies Station">
</p>

## ✨ Key Features

* **⏱️ Real-Time:** Accurate arrival times for Metro (TMB), Bus (TMB, AMB), Rodalies, FGC, Tram and Bicing.
* **🗺️ Vector Maps:** Smooth visualization of stops and routes using **MapLibre**.
* **♿ Accessibility:** Visual indicators on the map regarding station accessibility (elevators/stairs).
* **🌓 Dynamic Theme:** Native support for Light Mode and Dark Mode, adapting to system settings.
* **🔎 Unified Search:** Fast search for lines and stations with local history.
* **📍 Geolocation:** Automatic detection of nearby stops.

## 🛠️ Tech Stack

The project follows modern Android development best practices (2024+):

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture
* **Maps:** [MapLibre Native SDK](https://maplibre.org/)
* **Concurrency & State:** Kotlin Coroutines & Flow
* **Networking:** Retrofit + OkHttp + Gson
* **Dependency Injection:** (Manual / Hilt - *adjust as needed*)
* **Navigation:** Jetpack Navigation Compose

## 🏗️ Architecture

The application is modularized by layers following the Separation of Concerns principle:

1.  **Data Layer:** Repositories, Data Sources (API/Local), and DTOs.
2.  **Domain Layer:** Business models and pure logic (Mappers).
3.  **UI Layer:** ViewModels (StateFlow) and Composables.

## 📱 Download

You can download the latest stable version directly from the [Releases](https://github.com/BCNTransit/BCN-Transit-App/releases) section or soon on the Google Play Store.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

