# 계층 분리 전 data 모듈의 규칙을 모듈·패키지에 맞춰 옮김
-keep class com.bluepig.alarm.database.** { *; }

# Keep Room classes
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
