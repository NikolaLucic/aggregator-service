package com.nikola.aggregatorservice.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.nikola.aggregatorservice.adapter.CreditCardAdapter
import com.nikola.aggregatorservice.domain.CardStatus
import com.nikola.aggregatorservice.domain.CardSummary
import com.nikola.aggregatorservice.domain.CardType
import com.nikola.aggregatorservice.domain.CreditCard
import com.nikola.aggregatorservice.exception.ServiceRuntimeException
import com.nikola.aggregatorservice.utils.DomainFactory
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class CreditCardServiceTest {

    @Mock
    private lateinit var creditCardAdapter: CreditCardAdapter

    private lateinit var underTest: CreditCardService

    @Before
    fun setUp() {
        underTest = CreditCardService(creditCardAdapter)
    }

    @Test
    fun `get cards details for poa with 2 credit cards test`() {
        val newCardId = 3
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(
                CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD),
                CardSummary(newCardId, CardType.CREDIT_CARD)
            )
        )
        whenever(creditCardAdapter.getCardById(DomainFactory.DEFAULT_CC_ID)).thenReturn(DomainFactory.getDefaultCreditCard())
        whenever(creditCardAdapter.getCardById(newCardId)).thenReturn(DomainFactory.getDefaultCreditCard().copy(id = newCardId))

        val result = runBlocking {
            underTest.getCreditCardDetailsAsync(poa).await()
        }

        verify(creditCardAdapter, times(2)).getCardById(any())
        assertEquals(2, result.size)
        checkCardDetails(result[0])
        assertEquals(DomainFactory.DEFAULT_CC_ID, result[0].id, "First CC id not mapped properly!")
        checkCardDetails(result[1])
        assertEquals(newCardId, result[1].id, "Second CC id not mapped properly!")

    }

    @Test
    fun `get cards details for poa with 1 credit and 1 debit card test`() {
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(
                CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD),
                CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD)
            )
        )
        whenever(creditCardAdapter.getCardById(DomainFactory.DEFAULT_CC_ID)).thenReturn(DomainFactory.getDefaultCreditCard())
        val result = runBlocking {
            underTest.getCreditCardDetailsAsync(poa).await()
        }

        verify(creditCardAdapter, times(1)).getCardById(DomainFactory.DEFAULT_CC_ID)
        assertEquals(1, result.size)
        checkCardDetails(result[0])
        assertEquals(DomainFactory.DEFAULT_CC_ID, result[0].id, "First CC id not mapped properly!")
    }

    @Test
    fun `adapter throws an exception test`() {
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD))
        )
        whenever(creditCardAdapter.getCardById(DomainFactory.DEFAULT_CC_ID)).thenThrow(ServiceRuntimeException::class.java)
        val result = runBlocking {
                underTest.getCreditCardDetailsAsync(poa).await()
            }

        verify(creditCardAdapter, times(1)).getCardById(DomainFactory.DEFAULT_CC_ID)
        assertEquals(0, result.size)

    }

    @Test
    fun `adapter returns null test`() {
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD))
        )
        whenever(creditCardAdapter.getCardById(DomainFactory.DEFAULT_CC_ID)).thenReturn(null)
        val result = runBlocking {
            underTest.getCreditCardDetailsAsync(poa).await()
        }

        verify(creditCardAdapter, times(1)).getCardById(DomainFactory.DEFAULT_CC_ID)
        assertEquals(0, result.size)
    }

    @Test
    fun `filter inactive cards test`() {
        val newCardId = 3
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(
                CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD),
                CardSummary(newCardId, CardType.CREDIT_CARD)
            )
        )
        whenever(creditCardAdapter.getCardById(DomainFactory.DEFAULT_CC_ID)).thenReturn(DomainFactory.getDefaultCreditCard())
        whenever(creditCardAdapter.getCardById(newCardId)).thenReturn(DomainFactory.getDefaultCreditCard().copy(id = newCardId, status = CardStatus.BLOCKED))

        val result = runBlocking {
            underTest.getCreditCardDetailsAsync(poa).await()
        }

        verify(creditCardAdapter, times(2)).getCardById(any())
        assertEquals(1, result.size)
        checkCardDetails(result[0])
        assertEquals(DomainFactory.DEFAULT_CC_ID, result[0].id, "First CC id not mapped properly!")

    }

    private fun checkCardDetails(creditCard: CreditCard) {
        assertEquals(DomainFactory.DEFAULT_CC_HOLDER, creditCard.cardHolder, "CC holder name not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_CC_MONTHLY_LIMIT, creditCard.monthlyLimit, "CC monthly limit not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_CC_NUMBER, creditCard.cardNumber, "CC number not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_CC_SEQUENCE_NMB, creditCard.sequenceNumber, "CC sequence number not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_CARD_STATUS, creditCard.status, "CC status not mapped properly!")

    }
}
