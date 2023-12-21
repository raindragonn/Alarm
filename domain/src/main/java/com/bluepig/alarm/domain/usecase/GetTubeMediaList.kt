package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.repository.TubeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTubeMediaList @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: TubeRepository
) {
    suspend operator fun invoke(query: String) = withContext(_dispatcher) {
        kotlin.runCatching {
            val result = mutableListOf<TubeMedia>()

            _repository.checkTubeMedia(query)?.let { tubeForIds ->
                result.add(tubeForIds)
            } ?: _repository.getTubeList(query).let {
                result.addAll(it)
            }

            result
        }
    }
}