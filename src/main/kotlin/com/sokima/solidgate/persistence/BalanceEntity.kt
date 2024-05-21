package com.sokima.solidgate.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("balances")
data class BalanceEntity(
    @Id @Column("account_id") val accountId: Int?,

    @Column("balance")
    val balance: Int
) : Persistable<Int> {
    override fun getId(): Int? {
        return accountId
    }

    override fun isNew(): Boolean {
        return true // it needs to force creation always
    }

}