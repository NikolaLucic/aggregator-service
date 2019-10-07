package com.nikola.aggregatorservice.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.nikola.aggregatorservice.utils.DomainFactory
import com.nikola.aggregatorservice.adapter.DebitCardAdapter
import com.nikola.aggregatorservice.adapter.PowerOfAttorneyAdapter
import com.nikola.aggregatorservice.api.EnrichedPowerOfAttorney
import com.nikola.aggregatorservice.exception.ServiceRuntimeException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertFailsWith

@RunWith(MockitoJUnitRunner::class)
class PowerOfAttorneyServiceTest {

    @Mock
    private lateinit var poaAdapter: PowerOfAttorneyAdapter

    private lateinit var underTest: PowerOfAttorneyService

    @Before
    fun setUp() {
        underTest = PowerOfAttorneyService(poaAdapter)
    }

    @Test
    fun `get power of attorney test`() {
        whenever(poaAdapter.getById(DomainFactory.DEFAULT_POA_ID)).thenReturn(DomainFactory.getDefaultPOA())

        val result = underTest.getPowerOfAttorneyById(DomainFactory.DEFAULT_POA_ID)
        verify(poaAdapter, times(1)).getById(DomainFactory.DEFAULT_POA_ID)

        assertNotNull(result)
        assertEquals(DomainFactory.getDefaultPOA(), result)
    }

    @Test
    fun `adapter throws exception test`() {
        whenever(poaAdapter.getById(DomainFactory.DEFAULT_POA_ID)).thenThrow(ServiceRuntimeException(1, "Excepition message."))

        val exception =
            assertFailsWith<ServiceRuntimeException> { underTest.getPowerOfAttorneyById(DomainFactory.DEFAULT_POA_ID) }
        verify(poaAdapter, times(1)).getById(DomainFactory.DEFAULT_POA_ID)

        assertEquals(1, exception.errorCode )
        assertEquals("Excepition message.", exception.message )

    }

}
