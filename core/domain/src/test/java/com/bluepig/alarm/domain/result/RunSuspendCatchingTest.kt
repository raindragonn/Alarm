package com.bluepig.alarm.domain.result

import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class RunSuspendCatchingTest {
    @Test
    fun `결과와 예외를 Result로 감싼다`() {
        val error = IllegalStateException()

        assertEquals(Result.success(1), runSuspendCatching { 1 })
        assertSame(error, runSuspendCatching { throw error }.exceptionOrNull())
    }

    @Test
    fun `취소는 삼키지 않고 다시 던진다`() {
        val cancellation = CancellationException()
        try {
            runSuspendCatching { throw cancellation }
            fail()
        } catch (e: CancellationException) {
            assertSame(cancellation, e)
        }
    }
}
