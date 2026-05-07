# WSC Sports Home Assignment - Android

A modern Android application that displays sports match highlights in an Instagram Stories-style video player.

## 📱 Features

- **Leagues Screen** (Jetpack Compose)
  - Displays matches grouped by league
  - Shows team logos, scores, and match status
  - Live match indicators
  - Pull-to-refresh functionality
  - Error handling with retry

- **Story Player** (XML + ExoPlayer)
  - Instagram Stories-style horizontal swipe
  - Auto-advance to next clip
  - Progress indicators for each clip
  - Smooth video playback with prefetching
  - Fullscreen immersive experience

## 🏗️ Architecture

This project follows **Clean Architecture** principles with **MVVM** pattern:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Activities, Fragments, Compose UI)    │
│         ViewModels, UI State            │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│    (Use Cases, Domain Models)           │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  (Repository, Remote/Local DataSource)  │
└─────────────────────────────────────────┘
```

### Key Components

- **Data Layer**: API service, DTOs, Repository implementation
- **Domain Layer**: Business models, Repository interface, Use cases
- **Presentation Layer**: ViewModels, Compose UI, XML layouts

## 🔧 Tech Stack

### Core
- **Kotlin** - Modern programming language
- **Jetpack Compose** - Modern declarative UI (Leagues screen)
- **XML Layouts** - Traditional Android UI (Player screen)
- **Coroutines** - Asynchronous programming
- **Flow** - Reactive data streams

### Architecture Components
- **ViewModel** - Lifecycle-aware state management
- **Hilt** - Dependency injection
- **Navigation** - Screen navigation

### Networking
- **Retrofit** - REST API client
- **OkHttp** - HTTP client with logging
- **Moshi** - JSON parsing

### Media
- **ExoPlayer (Media3)** - Professional video playback
- **ViewPager2** - Swipe between clips

### Image Loading
- **Coil** - Modern image loading with caching

### Utilities
- **Timber** - Logging

## 📋 Requirements Met

✅ Display matches grouped by league  
✅ Show final scores from last page  
✅ Filter games without `wscGame` or `primeStory`  
✅ Instagram Stories-style horizontal player  
✅ Swipe between video clips  
✅ Root app: XML-based  
✅ Leagues screen: Jetpack Compose  
✅ Player screen: XML  
✅ Clean architecture with best practices  
✅ Prefetching and caching for smooth UX  

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 11 or higher
- Android SDK 24+ (Android 7.0+)

### Installation

1. Clone the repository
```bash
git clone <repository-url>
cd WSCHomeAssignment
```

2. Open in Android Studio
```
File > Open > Select project folder
```

3. Sync Gradle
```
File > Sync Project with Gradle Files
```

4. Run the app
```
Run > Run 'app'
```

## 📱 How to Use

1. **Launch the app** - You'll see the Leagues screen with matches grouped by league
2. **Tap any match** - Opens the Story Player with video highlights
3. **Swipe left/right** - Navigate between video clips
4. **Auto-advance** - Videos automatically advance to the next clip
5. **Close player** - Tap the X button or swipe down

## 🎯 Design Decisions

### Why Clean Architecture?
- **Separation of Concerns**: Each layer has a single responsibility
- **Testability**: Easy to write unit tests for each layer
- **Maintainability**: Changes in one layer don't affect others
- **Scalability**: Easy to add new features

### Why Jetpack Compose for Leagues?
- Modern declarative UI
- Less boilerplate code
- Better performance
- Easier state management

### Why XML for Player?
- Better control over ExoPlayer integration
- Proven stability for video playback
- Easier gesture handling

### Why ExoPlayer?
- Industry-standard video player
- Adaptive streaming support
- Excellent caching capabilities
- Smooth playback experience

### Data Filtering Strategy
```kotlin
// Filter at the data layer during mapping
games.filter { game ->
    game.wscGame != null && 
    game.wscGame.primeStory != null &&
    game.wscGame.primeStory.pages.isNotEmpty()
}
```

### Final Score Extraction
```kotlin
// Get score from last page as per requirements
val lastPage = match.wscGame.primeStory.pages.lastOrNull()
val homeScore = lastPage?.homeScore ?: 0
val awayScore = lastPage?.awayScore ?: 0
```

## 🎨 UI/UX Highlights

- **Material Design 3** - Modern, clean interface
- **Smooth animations** - Polished user experience
- **Loading states** - Clear feedback during data fetch
- **Error handling** - User-friendly error messages with retry
- **Live indicators** - Red dot for live matches
- **Progress bars** - Instagram-style progress indicators
- **Immersive player** - Fullscreen video experience

## 🔍 Performance Optimizations

1. **Lazy Loading** - LazyColumn for efficient list rendering
2. **Image Caching** - Coil handles memory and disk caching
3. **Video Prefetching** - Next video preloaded for smooth transition
4. **Coroutines** - Non-blocking async operations
5. **StateFlow** - Efficient state management

## 🧪 Testing

The architecture supports easy testing:

- **Unit Tests**: ViewModels, Use Cases, Repository
- **Integration Tests**: API service with MockWebServer
- **UI Tests**: Compose and Espresso tests

## 📝 Assumptions

1. **Network**: App requires internet connection (no offline mode in v1)
2. **Video Format**: All videos are MP4 compatible with ExoPlayer
3. **API Stability**: JSON endpoint is always accessible
4. **Device**: Minimum Android 7.0 (API 24)
5. **Orientation**: Portrait mode only
6. **Language**: English only (can be localized)

## 🐛 Known Limitations

1. **No Offline Support**: Requires active internet connection
2. **No Background Playback**: Videos stop when app is backgrounded
3. **Portrait Only**: Landscape mode not implemented
4. **Basic Error Recovery**: Simple retry mechanism

## 🚀 Future Enhancements

- [ ] Offline mode with local caching
- [ ] Favorites and bookmarks
- [ ] Search functionality
- [ ] Filters (by date, league, team)
- [ ] Push notifications for match updates
- [ ] Picture-in-Picture mode
- [ ] Chromecast support
- [ ] Analytics integration
- [ ] Multi-language support

## 📚 Project Structure

```
app/src/main/java/com/maxdroid/lord/wschomeassignment/
├── data/
│   ├── remote/
│   │   ├── api/          # Retrofit API service
│   │   └── dto/          # Data Transfer Objects
│   └── repository/       # Repository implementation
├── domain/
│   ├── model/            # Domain models
│   ├── repository/       # Repository interface
│   └── usecase/          # Business logic
├── presentation/
│   ├── leagues/          # Leagues screen (Compose)
│   ├── player/           # Story player (XML)
│   └── theme/            # Compose theme
├── di/                   # Dependency injection
└── util/                 # Utilities
```

## 📄 License

This project is created for the WSC Sports home assignment.

## 👤 Author

Senior Android Engineer

---

**Note**: This is a production-ready implementation following Android best practices and modern architecture patterns.
