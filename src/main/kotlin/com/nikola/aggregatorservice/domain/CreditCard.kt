package com.nikola.aggregatorservice.domain

import com.google.gson.Gson

data class CreditCard(
    override val id: Int,
    override val status: CardStatus,
    override val cardNumber: String,
    override val sequenceNumber: Int,
    override val cardHolder: String,
    val monthlyLimit: Int
) : Card {
    fun asJson() = Gson().toJson(this)
}
