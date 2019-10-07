package com.nikola.aggregatorservice.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.nikola.aggregatorservice.adapter.DebitCardAdapter
import com.nikola.aggregatorservice.domain.CardStatus
import com.nikola.aggregatorservice.domain.CardSummary
import com.nikola.aggregatorservice.domain.CardType
import com.nikola.aggregatorservice.domain.DebitCard
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
class DebitCardServiceTest {

    @Mock
    private lateinit var debitCardAdapter: DebitCardAdapter

    private lateinit var underTest: DebitCardService

    @Before
    fun setUp() {
        underTest = DebitCardService(debitCardAdapter)
    }

    @Test
    fun `get cards details for poa with 2 debit cards test`() {
        val newCardId = 3
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(
                CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD),
                CardSummary(newCardId, CardType.DEBIT_CARD)
            )
        )
        whenever(debitCardAdapter.getCardById(DomainFactory.DEFAULT_DC_ID)).thenReturn(DomainFactory.getDefaultDebitCard())
        whenever(debitCardAdapter.getCardById(newCardId)).thenReturn(DomainFactory.getDefaultDebitCard().copy(id = newCardId))

        val result = runBlocking {
            underTest.getDebitCardDetailsAsync(poa).await()
        }

        verify(debitCardAdapter, times(2)).getCardById(any())
        assertEquals(2, result.size)
        checkCardDetails(result[0])
        assertEquals(DomainFactory.DEFAULT_DC_ID, result[0].id, "First DC id not mapped properly!")
        checkCardDetails(result[1])
        assertEquals(newCardId, result[1].id, "Second DC id not mapped properly!")

    }

    @Test
    fun `get cards details for poa with 1 debit and 1 credit card test`() {
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(
                CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD),
                CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD)
            )
        )
        whenever(debitCardAdapter.getCardById(DomainFactory.DEFAULT_DC_ID)).thenReturn(DomainFactory.getDefaultDebitCard())
        val result = runBlocking {
            underTest.getDebitCardDetailsAsync(poa).await()
        }

        verify(debitCardAdapter, times(1)).getCardById(DomainFactory.DEFAULT_DC_ID)
        assertEquals(1, result.size)
        checkCardDetails(result[0])
        assertEquals(DomainFactory.DEFAULT_DC_ID, result[0].id, "DC id not mapped properly!")
    }

    @Test
    fun `adapter throws an exception test`() {
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD))
        )
        whenever(debitCardAdapter.getCardById(DomainFactory.DEFAULT_DC_ID)).thenThrow(ServiceRuntimeException::class.java)
        val result = runBlocking {
            underTest.getDebitCardDetailsAsync(poa).await()
        }

        verify(debitCardAdapter, times(1)).getCardById(DomainFactory.DEFAULT_DC_ID)
        assertEquals(0, result.size)

    }

    @Test
    fun `adapter returns null test`() {
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD))
        )
        whenever(debitCardAdapter.getCardById(DomainFactory.DEFAULT_DC_ID)).thenReturn(null)
        val result = runBlocking {
            underTest.getDebitCardDetailsAsync(poa).await()
        }

        verify(debitCardAdapter, times(1)).getCardById(DomainFactory.DEFAULT_DC_ID)
        assertEquals(0, result.size)
    }

    @Test
    fun `filter inactive cards test`() {
        val poa = DomainFactory.getDefaultPOA().copy(
            cards = listOf(
                CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD)
            )
        )
        whenever(debitCardAdapter.getCardById(DomainFactory.DEFAULT_DC_ID)).thenReturn(DomainFactory.getDefaultDebitCard().copy(status = CardStatus.BLOCKED))

        val result = runBlocking {
            underTest.getDebitCardDetailsAsync(poa).await()
        }

        verify(debitCardAdapter, times(1)).getCardById(any())
        assertEquals(0, result.size, "Blocked card should not be returned!")
    }

    private fun checkCardDetails(debitCard: DebitCard) {
        assertEquals(DomainFactory.DEFAULT_DC_HOLDER, debitCard.cardHolder, "DC holder name not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_DC_NUMBER, debitCard.cardNumber, "DC number not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_DC_SEQUENCE_NMB, debitCard.sequenceNumber, "DC sequence number not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_CARD_STATUS, debitCard.status, "DC status not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_DC_ATM_LIMIT, debitCard.atmLimit, "DC atm limit not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_DC_POS_LIMIT, debitCard.posLimit, "DC pos limit not mapped properly!")
        assertEquals(DomainFactory.DEFAULT_DC_CONTACTLESS, debitCard.contactless, "DC contactless not mapped properly!")
    }
}
