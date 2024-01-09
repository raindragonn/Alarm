# Media Alarm

<div align="center">
<img src="https://github.com/raindragonn/Alarm/assets/48205909/61abaea3-f9ad-422b-a9c6-9f2bab5bcba6" width="200"/>

## 다양한 미디어를 사용할 수 있는 알람 앱입니다.

[Youtube Data Api](https://developers.google.com/youtube/v3?hl=ko)
및 [4shared](https://www.4shared.com/developer/docs) 를 통해 원하는 미디어를 통해 하루를 시작할 수 있는 알람 앱입니다.
<br/>

Youtube
재생은 [android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player), 노래
재생은 [Media3](https://developer.android.com/jetpack/androidx/releases/media3)를 이용합니다.

구성은 MVVM + Clean Architecture 로 이루어져 있습니다.

### [Play Store Link](https://play.google.com/store/apps/details?id=com.bluepig.alarm)

</div>

## Info & Library

### Version Info

|           | Version |
|-----------|:-------:|
| MinSdk    |   24    |
| TargetSdk |   34    |
| Kotlin    |  1.9.0  |

### Library

| Library                                                                                 | Description                                                                                                                                    |
|-----------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| [Navigation](https://developer.android.com/guide/navigation/navigation-getting-started) | 다양한 화면 구성                                                                                                                                      |
| [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)        | 의존성 주입                                                                                                                                         |
| [Coroutine](https://developer.android.com/kotlin/coroutines)                            | [Flow](https://developer.android.com/kotlin/flow), [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) 등 통한 비동기 작업 |
| [Room](https://developer.android.com/training/data-storage/room)                        | 내부  저장소                                                                                                                                        |
| [Retrofit2](https://square.github.io/retrofit/)                                         | REST API                                                                                                                                       |
| [OkHttp3](https://square.github.io/okhttp/)                                             | API 통신 로그                                                                                                                                      |
| [Jsoup](https://jsoup.org/)                                                             | Html parser                                                                                                                                    |
| [Media3 - exoplayer](https://developer.android.com/guide/topics/media/exoplayer)        | 알람음 재생                                                                                                                                         |
| [youtube player](https://github.com/PierfrancescoSoffritti/android-youtube-player)      | 유튜브 영상 재생                                                                                                                                      |
