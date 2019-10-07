package com.nikola.aggregatorservice.service

import com.nikola.aggregatorservice.adapter.DebitCardAdapter
import com.nikola.aggregatorservice.domain.CardStatus
import com.nikola.aggregatorservice.domain.CardType
import com.nikola.aggregatorservice.domain.PowerOfAttorney
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DebitCardService(
    val debitCardAdapter: DebitCardAdapter
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getDebitCardDetailsAsync(poa: PowerOfAttorney?) = GlobalScope.async {
        poa?.let {
            it.cards?.filter { card -> card.type == CardType.DEBIT_CARD }
                ?.map { cardSummary -> cardSummary.id }
                ?.map { GlobalScope.async { debitCardAdapter.getCardById(it) } }
                ?.mapNotNull {
                    try {
                        it.await()
                    } catch (e: Exception) {
                        logger.error("Exception while getting debit card details for POA ${poa.id}. Exception message: ${e.message}")
                        null
                    }
                }
                ?.filter { it.status == CardStatus.ACTIVE }
        } ?: emptyList()
    }
}
