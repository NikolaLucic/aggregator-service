package com.nikola.aggregatorservice.api

import com.nikola.aggregatorservice.domain.*

//basically this would be defined in API (driver) of this service
// but I didn't had time to deal with additional driver project, sorry
data class EnrichedPowerOfAttorney(
    val id: Int,
    val grantor: String,
    val grantee: String,
    val account: String,
    val direction: String,
    val authorizations: List<Authorization> = listOf(),
    val debitCards: List<DebitCard> = listOf(),
    val creditCards: List<CreditCard> = listOf(),
    var messages: List<String> = mutableListOf()
) {
    constructor(poa: PowerOfAttorney, debitCards: List<DebitCard>, creditCards: List<CreditCard>) : this(
        id = poa.id,
        grantor = poa.grantor,
        grantee = poa.grantee,
        account = poa.account,
        direction = poa.direction,
        authorizations = poa.authorizations,
        debitCards = debitCards,
        creditCards = creditCards
    ) {
        if (poa.cards?.size ?: 0 > debitCards.size + creditCards.size) {
            val missingCardDetailIds = poa.cards?.map(CardSummary::id)
                ?.minus(debitCards.map(DebitCard::id).plus(creditCards.map(CreditCard::id)))
            messages = missingCardDetailIds?.map { "Unable to retrive card $it details!" } ?: listOf()
        }
    }
}
