package com.sokima.solidgate.persistence

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface BalanceRepository : ReactiveCrudRepository<BalanceEntity, Int>


