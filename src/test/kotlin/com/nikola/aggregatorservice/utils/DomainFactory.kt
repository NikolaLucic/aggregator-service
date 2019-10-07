package com.nikola.aggregatorservice.utils

import com.google.gson.Gson
import com.nikola.aggregatorservice.domain.*

object DomainFactory {

    const val DEFAULT_POA_ID = 1
    const val DEFAULT_POA_GRANTOR = "testGrantor"
    const val DEFAULT_POA_GRANTEE = "testGrantee"
    const val DEFAULT_POA_ACCOUNT = "testAccount"
    const val DEFAULT_POA_DIRECTION = "testDirection"
    val DEFAULT_POA_AUTHORISATIONS = listOf(Authorization.DEBIT_CARD)
    val DEFAULT_POA_CARDS = listOf(CardSummary(1, CardType.DEBIT_CARD))

    const val DEFAULT_CC_ID = 22
    val DEFAULT_CARD_STATUS = CardStatus.ACTIVE
    const val DEFAULT_CC_NUMBER = "11223344"
    const val DEFAULT_CC_SEQUENCE_NMB = 1
    const val DEFAULT_CC_HOLDER = "creditTest"
    const val DEFAULT_CC_MONTHLY_LIMIT = 1000

    const val DEFAULT_DC_ID = 33
    const val DEFAULT_DC_NUMBER = "55667788"
    const val DEFAULT_DC_SEQUENCE_NMB = 2
    const val DEFAULT_DC_HOLDER = "debitTest"
    val DEFAULT_DC_ATM_LIMIT = Limit(100, "PER_DAY")
    val DEFAULT_DC_POS_LIMIT = Limit(2000, "PER_MONTH")
    const val DEFAULT_DC_CONTACTLESS = true

    fun getDefaultPOA() = PowerOfAttorney(DEFAULT_POA_ID,
        DEFAULT_POA_GRANTOR,
        DEFAULT_POA_GRANTEE,
        DEFAULT_POA_ACCOUNT,
        DEFAULT_POA_DIRECTION,
        DEFAULT_POA_AUTHORISATIONS,
        DEFAULT_POA_CARDS)

    fun getDefaultCreditCard() = CreditCard(
        DEFAULT_CC_ID,
        DEFAULT_CARD_STATUS,
        DEFAULT_CC_NUMBER,
        DEFAULT_CC_SEQUENCE_NMB,
        DEFAULT_CC_HOLDER,
        DEFAULT_CC_MONTHLY_LIMIT
    )

    fun getDefaultDebitCard() = DebitCard(
        DEFAULT_DC_ID,
        DEFAULT_CARD_STATUS,
        DEFAULT_DC_NUMBER,
        DEFAULT_DC_SEQUENCE_NMB,
        DEFAULT_DC_HOLDER,
        DEFAULT_DC_ATM_LIMIT,
        DEFAULT_DC_POS_LIMIT,
        DEFAULT_DC_CONTACTLESS
    )
}
