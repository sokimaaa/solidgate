package com.sokima.solidgate.service

import com.sokima.solidgate.dto.UpdateChunk
import com.sokima.solidgate.persistence.BalanceEntity
import com.sokima.solidgate.persistence.BalanceRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class UpdateBalanceServiceTest {

    @Mock
    lateinit var balanceRepository: BalanceRepository

    @InjectMocks
    lateinit var updateBalanceService: UpdateBalanceService

    @Captor
    lateinit var captor: ArgumentCaptor<List<BalanceEntity>>

    @Test
    fun `should batch update the chunk`() {
        val balances = (1..500).associateWith { it * 10 }
        val updateChunk = UpdateChunk(balances)
        val balanceEntities = updateChunk.chunk.entries.map { (accountId, balance) ->
            BalanceEntity(accountId, balance)
        }

        `when`(balanceRepository.saveAll(captor.capture())).thenReturn(Flux.fromIterable(balanceEntities))

        updateBalanceService.updateBalanceByChunk(updateChunk)
            .log()
            .`as`(StepVerifier::create)
            .expectNextCount(500)
            .verifyComplete()

        verify(balanceRepository).saveAll(anyList())
        Assertions.assertEquals(500, captor.value.size)
    }
}