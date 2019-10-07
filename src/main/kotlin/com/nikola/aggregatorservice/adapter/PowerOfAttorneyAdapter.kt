package com.nikola.aggregatorservice.adapter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nikola.aggregatorservice.domain.PowerOfAttorney
import com.nikola.aggregatorservice.exception.ServiceRuntimeException
import com.nikola.aggregatorservice.util.AdapterUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.text.DecimalFormat

@Component
class PowerOfAttorneyAdapter(
    @Value("\${service.host}") val host: String,
    @Value("\${service.port}") val port: String,
    val adapterUtils: AdapterUtils
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getById(id: Int): PowerOfAttorney? {
        val decimalFormat = DecimalFormat("0000") //I love connecting to this kind of APIs
        val request = adapterUtils.createGetRequest("$host:$port/power-of-attorneys/${decimalFormat.format(id)}")
        try {
            return Gson().fromJson(adapterUtils.getResponse(request, "POA"), PowerOfAttorney::class.java)
        } catch (e: JsonSyntaxException) {
            logger.error("Error while retrieving power of attorney $id. Error message: ${e.message}", e)
            throw ServiceRuntimeException(
                500_006,
                "Unable to retrive power of attorney from POA service. Error message: ${e.message}",
                e
            )
        }
    }
}
