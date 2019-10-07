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
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.*

@RunWith(MockitoJUnitRunner::class)
class PowerOfAttorneyAdapterTest {

    @Mock
    private lateinit var adapterUtils: AdapterUtils
    @Mock
    private lateinit var httpRequest: HttpRequest

    private lateinit var underTest: PowerOfAttorneyAdapter

    @Before
    fun setUp() {
        underTest = PowerOfAttorneyAdapter("http://test", "1234", adapterUtils)
    }

    @Test
    fun `get by existing id test`() {
        whenever(adapterUtils.createGetRequest(any())).thenReturn(httpRequest)
        whenever(adapterUtils.getResponse(any(), any())).thenReturn(DomainFactory.getDefaultPOA().asJson())

        val result = underTest.getById(1)

        assertNotNull(result)
        assertEquals(DomainFactory.getDefaultPOA(), result)
    }

    @Test
    fun `get by non existing id test`() {
        whenever(adapterUtils.createGetRequest(any())).thenReturn(httpRequest)
        whenever(adapterUtils.getResponse(any(), any()))
            .thenThrow(ServiceRuntimeException(400_001, "Exception message"))

        val exception = assertFailsWith<ServiceRuntimeException> {  underTest.getById(1)}

        assertEquals(400_001, exception.errorCode)
    }

    @Test
    fun `get by id, json conversion exception test`() {
        whenever(adapterUtils.createGetRequest(any())).thenReturn(httpRequest)
        whenever(adapterUtils.getResponse(any(), any())).thenReturn("{some wrong json}")

        val exception = assertFailsWith<ServiceRuntimeException> {underTest.getById(1)  }

        assertEquals(500_006, exception.errorCode)
        assertTrue(exception.message?.startsWith("Unable to retrive power of attorney from POA service.") ?: false)
    }
}
