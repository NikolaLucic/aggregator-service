package com.nikola.aggregatorservice.domain

import com.google.gson.Gson

data class DebitCard(
    override val id: Int,
    override val status: CardStatus,
    override val cardNumber: String,
    override val sequenceNumber: Int,
    override val cardHolder: String,
    val atmLimit: Limit,
    val posLimit: Limit,
    val contactless: Boolean
) : Card {
    fun asJson() = Gson().toJson(this)
}
