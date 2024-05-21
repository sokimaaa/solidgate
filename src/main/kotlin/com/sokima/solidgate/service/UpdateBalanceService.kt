package com.sokima.solidgate.service

import com.sokima.solidgate.dto.UpdateChunk
import com.sokima.solidgate.persistence.BalanceEntity
import com.sokima.solidgate.persistence.BalanceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UpdateBalanceService(
    private val repository: BalanceRepository
) {
    fun updateBalanceByChunk(updateChunk: UpdateChunk): Flux<BalanceEntity> {
        return Mono.just(updateChunk)
            .map { chunk ->
                mapToBalanceEntities(chunk)
            }
            .flatMapMany { balances ->
                repository.saveAll(balances)
            }
            .log()
    }

    private fun mapToBalanceEntities(updateChunk: UpdateChunk): List<BalanceEntity> {
        return updateChunk.chunk.entries.map { (accountId, balance) ->
            BalanceEntity(accountId, balance)
        }
    }
}