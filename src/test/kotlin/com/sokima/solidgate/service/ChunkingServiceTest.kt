package com.sokima.solidgate.service

import com.sokima.solidgate.dto.BatchBalanceUpdateRequest
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class ChunkingServiceTest {

    @Test
    fun `should chunking the request`() {
        val chunkingService = ChunkingService(1000)
        val balances = (1..2500).associateWith { it * 10 }
        val batchRequest = BatchBalanceUpdateRequest(balances)

        chunkingService.chunkBatchBalanceUpdateRequest(batchRequest)
            .log()
            .`as`(StepVerifier::create)
            .expectNextCount(3)
            .verifyComplete()
    }
}