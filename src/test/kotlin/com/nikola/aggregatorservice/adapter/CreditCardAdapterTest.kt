package com.nikola.aggregatorservice.adapter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.nikola.aggregatorservice.exception.ServiceRuntimeException
import com.nikola.aggregatorservice.util.AdapterUtils
import com.nikola.aggregatorservice.utils.DomainFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.net.http.HttpRequest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class CreditCardAdapterTest {

    @Mock
    private lateinit var adapterUtils: AdapterUtils
    @Mock
    private lateinit var httpRequest: HttpRequest

    private lateinit var underTest: CreditCardAdapter

    @Before
    fun setUp() {
        underTest = CreditCardAdapter("http://creditTest", "1234", adapterUtils)
    }

    @Test
    fun `get by existing id test`() {
        whenever(adapterUtils.createGetRequest(any())).thenReturn(httpRequest)
        whenever(adapterUtils.getResponse(any(), any())).thenReturn(DomainFactory.getDefaultCreditCard().asJson())

        val result = underTest.getCardById(DomainFactory.DEFAULT_CC_ID)

        assertNotNull(result)
        assertEquals(DomainFactory.getDefaultCreditCard(), result)
    }

    @Test
    fun `get by non existing id test`() {
        whenever(adapterUtils.createGetRequest(any())).thenReturn(httpRequest)
        whenever(adapterUtils.getResponse(any(), any()))
            .thenThrow(ServiceRuntimeException(300_001, "Exception message"))

        val exception = assertFailsWith<ServiceRuntimeException> {  underTest.getCardById(DomainFactory.DEFAULT_CC_ID)}

        assertEquals(300_001, exception.errorCode)
    }

    @Test
    fun `get by id, json conversion exception test`() {
        whenever(adapterUtils.createGetRequest(any())).thenReturn(httpRequest)
        whenever(adapterUtils.getResponse(any(), any())).thenReturn("{some wrong json}")

        val exception = assertFailsWith<ServiceRuntimeException> {underTest.getCardById(DomainFactory.DEFAULT_CC_ID)  }

        assertEquals(500_004, exception.errorCode)
        assertTrue(exception.message?.startsWith("Unable to retrive credit card ${DomainFactory.DEFAULT_CC_ID}") ?: false)
    }
}