package com.bluepig.alarm.data

import com.bluepig.alarm.domain.result.DataSourceException
import com.bluepig.alarm.domain.result.NetworkUnavailableException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// UserRecoverableAuthIOException → TubeAuthRequiredException은 Android Intent가 필요해 기기에서 확인 (기술 0003)
class DataErrorsTest {
    private fun mapped(error: Exception): Throwable =
        runCatching { mapDataErrors { throw error } }.exceptionOrNull()!!

    @Test
    fun `시간 초과와 호스트를 못 찾는 경우만 네트워크 오류`() {
        for (error in listOf(SocketTimeoutException(), UnknownHostException())) {
            val mapped = mapped(error)
            assertTrue(mapped is NetworkUnavailableException)
            assertSame(error, mapped.cause)
        }
    }

    @Test
    fun `그 밖의 IO·HTTP·형식 오류는 데이터 오류`() {
        val errors = listOf(
            IOException(),
            HttpException(Response.error<Unit>(500, "".toResponseBody())),
            SerializationException(),
        )
        for (error in errors) {
            val mapped = mapped(error)
            assertTrue(mapped is DataSourceException)
            assertSame(error, mapped.cause)
        }
    }

    @Test
    fun `취소와 예상하지 못한 예외는 그대로 던진다`() {
        val cancellation = CancellationException()
        val bug = IllegalStateException()

        assertSame(cancellation, mapped(cancellation))
        assertSame(bug, mapped(bug))
    }
}
