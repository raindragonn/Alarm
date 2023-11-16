package com.bluepig.alarm.domain.result

object LoadingException : Exception("Could Not getting data for Loading State")

object FailureException : Exception("BpResult is Failure")

object NotFoundActiveAlarmException : Exception("Not Found Active Alarm")

object NotFoundAlarmException : Exception("Not Found Alarm")

object SearchQueryEmptyException : Exception("Search Query is Empty or blank")

object NotFoundPlayerException :
    NullPointerException("Player is Not Found, Not initialized or a release")