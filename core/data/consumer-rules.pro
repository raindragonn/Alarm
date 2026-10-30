# 계층 분리 전 data 모듈의 규칙을 모듈·패키지에 맞춰 옮김
-keep class com.bluepig.alarm.data.** { *; }

# Keep Hilt generated classes
-keep class * extends dagger.hilt.** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
