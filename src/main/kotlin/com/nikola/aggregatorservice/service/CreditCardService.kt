package com.nikola.aggregatorservice.service

import com.nikola.aggregatorservice.adapter.CreditCardAdapter
import com.nikola.aggregatorservice.domain.CardStatus
import com.nikola.aggregatorservice.domain.CardType
import com.nikola.aggregatorservice.domain.CreditCard
import com.nikola.aggregatorservice.domain.PowerOfAttorney
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CreditCardService(val creditCardAdapter: CreditCardAdapter) {

    private val logger = LoggerFactory.getLogger(javaClass)

    //sorry for all this safe calls ( I had to make cards field nullable because GSON don't know
    //to handle Kotlin default values). There is https://github.com/square/moshi/blob/master/README.md
    //but I haven't had time to get to know well this library

    fun getCreditCardDetailsAsync(poa: PowerOfAttorney?) = GlobalScope.async {
        poa?.let {
            it.cards?.filter { card -> card.type == CardType.CREDIT_CARD }
                ?.map { cardSummary -> cardSummary.id }
                ?.map { GlobalScope.async { creditCardAdapter.getCardById(it) } }
                ?.mapNotNull {
                    try {
                        it.await()
                    } catch (e: Exception) {
                        logger.error("Exception while getting credit card details for ${poa.id} Exception message: ${e.message}")
                        null
                    }
                }
                ?.filter { it.status == CardStatus.ACTIVE }
        } ?: emptyList()
    }
}
