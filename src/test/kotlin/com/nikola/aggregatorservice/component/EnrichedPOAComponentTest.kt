package com.nikola.aggregatorservice.component

import com.nhaarman.mockito_kotlin.whenever
import com.nikola.aggregatorservice.controller.EnrichedPOAController
import com.nikola.aggregatorservice.domain.CardSummary
import com.nikola.aggregatorservice.domain.CardType
import com.nikola.aggregatorservice.util.AdapterUtils
import com.nikola.aggregatorservice.utils.DomainFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException
import java.net.http.HttpClient
import java.net.http.HttpResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@RunWith(SpringRunner::class)
@SpringBootTest
class EnrichedPOAComponentTest {

    @MockBean
    private lateinit var httpClient: HttpClient
    @MockBean(name = "posResponse")
    private lateinit var posResponse: HttpResponse<String>
    @MockBean(name = "ccResponse")
    private lateinit var ccResponse: HttpResponse<String>
    @MockBean(name = "dcResponse")
    private lateinit var dcResponse: HttpResponse<String>

    @Autowired
    private lateinit var enrichedPOAController: EnrichedPOAController

    @Autowired
    private lateinit var adapterUtils: AdapterUtils

    @Value("\${service.host}")
    private lateinit var host: String
    @Value("\${service.port}")
    private lateinit var port: String

    @Before
    fun setUp() {
        val poaRequest = adapterUtils.createGetRequest("$host:$port/power-of-attorneys/0001")
        val dcRequest = adapterUtils.createGetRequest("$host:$port/debit-cards/${DomainFactory.DEFAULT_DC_ID}")
        val ccRequest = adapterUtils.createGetRequest("$host:$port/credit-cards/${DomainFactory.DEFAULT_CC_ID}")

        whenever(httpClient.send(poaRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(posResponse)
        whenever(httpClient.send(dcRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(dcResponse)
        whenever(httpClient.send(ccRequest, HttpResponse.BodyHandlers.ofString())).thenReturn(ccResponse)
    }

    @Test
    fun `get enriched poa with two cards test`() {
        val cards = listOf(
            CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD),
            CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD)
        )

        whenever(posResponse.statusCode()).thenReturn(200)
        whenever(ccResponse.statusCode()).thenReturn(200)
        whenever(dcResponse.statusCode()).thenReturn(200)

        whenever(posResponse.body()).thenReturn(DomainFactory.getDefaultPOA().copy(cards = cards).asJson())
        whenever(dcResponse.body()).thenReturn(DomainFactory.getDefaultDebitCard().asJson())
        whenever(ccResponse.body()).thenReturn(DomainFactory.getDefaultCreditCard().asJson())

        val result = enrichedPOAController.getPOAById(DomainFactory.DEFAULT_POA_ID).body

        assertNotNull(result, "Result is null!")
        assertTrue(result.messages.isEmpty(), "There are ${result.messages.size} messages! Expected no messages in the response. ")
        assertEquals(1, result.debitCards.size, "One debit card expected in the result!")
        assertEquals(DomainFactory.getDefaultDebitCard(), result.debitCards[0], "Debit card is not sa expected!")
        assertEquals(1, result.creditCards.size, "One credit card expected in the result!")
        assertEquals(DomainFactory.getDefaultCreditCard(), result.creditCards[0], "Credit card is not as expected!")
    }

    @Test
    fun `server error when retriving debit card test`() {
        val cards = listOf(
            CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD),
            CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD)
        )

        whenever(posResponse.statusCode()).thenReturn(200)
        whenever(ccResponse.statusCode()).thenReturn(200)
        whenever(dcResponse.statusCode()).thenReturn(500)

        whenever(posResponse.body()).thenReturn(DomainFactory.getDefaultPOA().copy(cards = cards).asJson())
        whenever(ccResponse.body()).thenReturn(DomainFactory.getDefaultCreditCard().asJson())

        val result = enrichedPOAController.getPOAById(DomainFactory.DEFAULT_POA_ID).body

        assertNotNull(result, "Result is null!")
        assertEquals(1, result.messages.size, "There are ${result.messages.size} messages! Expected 1 message in the response.")
        assertEquals("Unable to retrive card ${DomainFactory.DEFAULT_DC_ID} details!", result.messages[0], "Error message in response is not correct!")
        assertEquals(0, result.debitCards.size, "No debit card expected in the result!")
        assertEquals(1, result.creditCards.size, "One credit card expected in the result!")
        assertEquals(DomainFactory.getDefaultCreditCard(), result.creditCards[0], "Credit card is not as expected!")
    }

    @Test
    fun `exception thrown when retriving credit card test`() {
        val ccRequest = adapterUtils.createGetRequest("$host:$port/credit-cards/${DomainFactory.DEFAULT_CC_ID}")
        whenever(httpClient.send(ccRequest, HttpResponse.BodyHandlers.ofString())).thenThrow(IOException::class.java)

        val cards = listOf(
            CardSummary(DomainFactory.DEFAULT_DC_ID, CardType.DEBIT_CARD),
            CardSummary(DomainFactory.DEFAULT_CC_ID, CardType.CREDIT_CARD)
        )

        whenever(posResponse.statusCode()).thenReturn(200)
        whenever(dcResponse.statusCode()).thenReturn(200)

        whenever(posResponse.body()).thenReturn(DomainFactory.getDefaultPOA().copy(cards = cards).asJson())
        whenever(dcResponse.body()).thenReturn(DomainFactory.getDefaultDebitCard().asJson())

        val result = enrichedPOAController.getPOAById(DomainFactory.DEFAULT_POA_ID).body

        assertNotNull(result, "Result is null!")
        assertEquals(1, result.messages.size, "There are ${result.messages.size} messages! Expected 1 message in the response.")
        assertEquals("Unable to retrive card ${DomainFactory.DEFAULT_CC_ID} details!", result.messages[0], "Error message in response is not correct!")
        assertEquals(0, result.creditCards.size, "No credit card expected in the result!")
        assertEquals(1, result.debitCards.size, "One credit card expected in the result!")
        assertEquals(DomainFactory.getDefaultDebitCard(), result.debitCards[0], "Debit card is not as expected!")
    }

}
