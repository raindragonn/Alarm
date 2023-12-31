package com.bluepig.alarm.domain.result

object LoadingException : Exception("Could Not getting data for Loading State")

object InitialException : Exception("Could Not getting data for Initial State")

object NotFoundActiveAlarmException : Exception("Not Found Active Alarm")

object NotFoundAlarmException : Exception("Not Found Alarm")

object NotSelectAlarmMedia : Exception("Not Found SongFile")

object NotFoundPreViewAlarmException : Exception("Not Found Preview Alarm")

object SearchQueryEmptyException : Exception("Search Query is Empty or blank")

object NotFoundPlayerException :
    NullPointerException("Player is Not Found, Not initialized or a release")

object NotFoundMediaItemException :
    NullPointerException("MediaItem is Not Found")

class AlarmSaveFailedException(id: Long) : NullPointerException("Not Found Saved Alarm..id:$id")

class NotFoundArgumentException(argumentName: String) :
    NullPointerException("Not Found $argumentName For NavArgs")