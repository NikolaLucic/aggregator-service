package com.nikola.aggregatorservice.service

import com.nikola.aggregatorservice.adapter.PowerOfAttorneyAdapter
import com.nikola.aggregatorservice.domain.*
import org.springframework.stereotype.Service

@Service
class PowerOfAttorneyService (val poaAdapter: PowerOfAttorneyAdapter) {

    fun getPowerOfAttorneyById(id: Int): PowerOfAttorney? {
        return poaAdapter.getById(id)
    }
}
