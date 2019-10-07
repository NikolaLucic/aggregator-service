package com.nikola.aggregatorservice.adapter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nikola.aggregatorservice.domain.DebitCard
import com.nikola.aggregatorservice.exception.ServiceRuntimeException
import com.nikola.aggregatorservice.util.AdapterUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DebitCardAdapter(
    @Value("\${service.host}") val host: String,
    @Value("\${service.port}") val port: String,
    val adapterUtils: AdapterUtils
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getCardById(id: Int): DebitCard? {
        val request = adapterUtils.createGetRequest("$host:$port/debit-cards/$id")
        try {
            return Gson().fromJson(adapterUtils.getResponse(request, "Debit card"), DebitCard::class.java)
        } catch (e: JsonSyntaxException) {
            logger.error("Error while retrieving debit card $id. Error message: ${e.message}", e)
            throw ServiceRuntimeException(
                500_005,
                "Unable to retrive debit card $id from debit card service. Error message: ${e.message}",
                e
            )
        }
    }
}
