# WSC Sports Home Assignment - Android

## 🏗️ Architecture

Clean Architecture + MVVM pattern with 3 layers:
- **Presentation**: ViewModels, Compose UI, XML layouts
- **Domain**: Use Cases, Business Models
- **Data**: Repository, API, DTOs

## 🏗️ Architecture Pattern: Clean Architecture + MVVM

### Why Clean Architecture?
- **Separation of Concerns**: Clear boundaries between layers
- **Testability**: Each layer can be tested independently
- **Maintainability**: Easy to modify without affecting other layers
- **Scalability**: Simple to add new features

## 🚀 Future Enhancements

1. **Offline Mode**: Cache matches for offline viewing
2. **Favorites**: Save favorite teams/leagues
3. **Notifications**: Match start reminders
4. **Search**: Find specific matches
5. **Filters**: By date, league, team
6. **Analytics**: Track user engagement
7. **Picture-in-Picture**: Continue watching while browsing
8. **Chromecast**: Cast to TV

## 📝 Assumptions

1. **API Availability**: JSON endpoint is always accessible
2. **Video Format**: All videos are MP4 compatible with ExoPlayer
3. **Network**: User has internet connection (no offline mode in v1)
4. **Device**: Minimum Android 7.0 (API 24)
5. **Orientation**: Portrait mode only (can be extended)
6. **Language**: localized to 5 languages only

## 🐛 Known Limitations

1. **No Offline Support**: Requires internet connection
2. **No Background Playback**: Videos stop when app is backgrounded
3. **Limited Error Recovery**: Basic retry mechanism
4. **No Analytics**: No usage tracking in v1

## 🔧 Tech Stack

- **Kotlin** 2.0.21
- **Jetpack Compose** + **XML**
- **Hilt** (DI)
- **Retrofit** + **Moshi** (Networking)
- **ExoPlayer/Media3** (Video)
- **Coil3** (Images)
- **Coroutines** + **Flow** (Async)
- **Material3** (Design)
- **ViewPager2** (Swipe)
- **Timber** (Logging)

## 📋 Requirements Met

### Core Requirements (11/11) ✅
✅ Display matches grouped by league  
✅ Filter games without wscGame/primeStory  
✅ Final score from last page  
✅ Horizontal swipe player  
✅ Root app: XML  
✅ Leagues: Compose  
✅ Player: XML  
✅ Prefetching & caching  
✅ Clean architecture  
✅ Open-source libraries  
✅ Technical design principles  

## 🎯 Key Features

### Performance
- **Instant loading** - first video < 1 second
- **Smart prefetching** - dual ExoPlayer for background loading
- **Optimized buffering** - 8s min, 25s max, 500ms start threshold
- **Image caching** - Coil automatic memory + disk cache
- **ViewPager2** - offscreenPageLimit=2 for prefetching

### User Experience
- **Collapsible leagues** - clean UI, tap to expand
- **Real-time progress** - bars fill as video plays
- **Auto-close** - returns to leagues when done
- **Gesture controls** - intuitive video control
- **Fallback placeholders** - handles missing logos gracefully
- **5 languages** - international support with RTL

### Code Quality
- **Clean Architecture** - proper separation of concerns
- **MVVM pattern** - reactive UI with Flow
- **Dependency Injection** - Hilt for testability
- **Type safety** - sealed classes, null safety
- **SOLID principles** - maintainable code

## 🌍 Supported Languages

| Language | Code |
|----------|------|
| English | en |
| Hebrew | he |
| Spanish | es |
| German | de |
| Portuguese | pt |

## 👤 Author

Maksim Syramalotau