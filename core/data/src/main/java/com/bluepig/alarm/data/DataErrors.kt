package com.bluepig.alarm.data

import com.bluepig.alarm.domain.result.DataSourceException
import com.bluepig.alarm.domain.result.NetworkUnavailableException
import com.bluepig.alarm.domain.result.TubeAuthRequiredException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 외부 라이브러리 예외를 domain 예외로 바꾼다 (기술 0016).
 * 화면의 토스트 분기를 바꾸지 않게 시간 초과·호스트 못 찾음만 네트워크 오류로 둔다
 */
internal inline fun <T> mapDataErrors(block: () -> T): T = try {
    block()
} catch (e: UserRecoverableAuthIOException) {
    throw TubeAuthRequiredException(e.intent)
} catch (e: SocketTimeoutException) {
    throw NetworkUnavailableException(e)
} catch (e: UnknownHostException) {
    throw NetworkUnavailableException(e)
} catch (e: IOException) {
    throw DataSourceException(e)
} catch (e: HttpException) {
    throw DataSourceException(e)
} catch (e: SerializationException) {
    throw DataSourceException(e)
}
