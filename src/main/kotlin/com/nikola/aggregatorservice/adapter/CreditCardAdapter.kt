package com.nikola.aggregatorservice.adapter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nikola.aggregatorservice.domain.CreditCard
import com.nikola.aggregatorservice.exception.ServiceRuntimeException
import com.nikola.aggregatorservice.util.AdapterUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CreditCardAdapter(@Value("\${service.host}") val host: String,
                        @Value("\${service.port}") val port: String,
                        val adapterUtils: AdapterUtils
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getCardById(id: Int): CreditCard? {
        val request = adapterUtils.createGetRequest("$host:$port/credit-cards/$id")
        try {
            return Gson().fromJson(adapterUtils.getResponse(request, "Credit card"), CreditCard::class.java)
        } catch (e: JsonSyntaxException) {
            logger.error("Error while retrieving credit card $id. Error message: ${e.message}", e)
            throw ServiceRuntimeException(
                500_004,
                "Unable to retrive credit card $id from credit card service. Error message: ${e.message}",
                e
            )
        }
    }
}
