package com.sokima.solidgate.service

import com.sokima.solidgate.dto.BatchBalanceUpdateRequest
import com.sokima.solidgate.dto.UpdateChunk
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChunkingService(
    @Value("\${chunk.size}") private val chunkSize: Int
) {
    fun chunkBatchBalanceUpdateRequest(batchRequest: BatchBalanceUpdateRequest): Flux<UpdateChunk> {
        return Flux.fromIterable(
            batchRequest.balances.entries.chunked(chunkSize)
                .map { chunk -> UpdateChunk(chunk.associate { it.toPair() }) }
        )
    }
}