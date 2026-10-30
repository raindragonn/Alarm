package com.bluepig.alarm.domain.result

/** 화면에 알리는 domain 오류. 외부 라이브러리 예외는 data가 이 타입으로 바꿔 던진다 (기술 0016) */
sealed class DomainException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)

object LoadingException : Exception("Could Not getting data for Loading State")

object InitialException : Exception("Could Not getting data for Initial State")

object NotFoundAlarmException : DomainException("Not Found Alarm")

object NotSelectAlarmMedia : DomainException("Not Found SongFile")

object NotFoundPreViewAlarmException : Exception("Not Found Preview Alarm")

object SearchQueryEmptyException : DomainException("Search Query is Empty or blank")

object NotFoundPlayerException :
    NullPointerException("Player is Not Found, Not initialized or a release")

object NotFoundMediaItemException :
    NullPointerException("MediaItem is Not Found")

class AlarmSaveFailedException(id: Long) : DomainException("Not Found Saved Alarm..id:$id")

class NotFoundArgumentException(argumentName: String) :
    NullPointerException("Not Found $argumentName For NavArgs")

/** 연결 시간 초과·호스트를 못 찾음 */
class NetworkUnavailableException(cause: Throwable) : DomainException(cause = cause)

/** 그 밖의 외부 데이터 오류 (HTTP 오류 응답, 응답 형식 오류 등) */
class DataSourceException(cause: Throwable) : DomainException(cause = cause)

/** YouTube 계정 재인증이 필요함. [resolution]은 화면이 실행할 Android Intent */
class TubeAuthRequiredException(val resolution: Any) : DomainException()
