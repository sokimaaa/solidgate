package com.sokima.solidgate.controller

import com.sokima.solidgate.dto.BatchBalanceUpdateRequest
import com.sokima.solidgate.persistence.BalanceEntity
import com.sokima.solidgate.service.ChunkingService
import com.sokima.solidgate.service.UpdateBalanceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class BalanceController(
    val chunkingService: ChunkingService,
    val updateBalanceService: UpdateBalanceService
) {
    @PostMapping("balances")
    fun batchUpdate(@RequestBody request: BatchBalanceUpdateRequest): Mono<ResponseEntity<List<BalanceEntity>>> {
        return Mono.just(request)
            .flatMapMany { req -> chunkingService.chunkBatchBalanceUpdateRequest(req) }
            .flatMap { chunk -> updateBalanceService.updateBalanceByChunk(chunk) }
            .collectList()
            .map { balances -> ResponseEntity.ok(balances) }
    }
}


