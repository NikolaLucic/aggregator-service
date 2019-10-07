package com.nikola.aggregatorservice.util

import com.nikola.aggregatorservice.exception.ServiceRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class AdapterUtils(val httpClient: HttpClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createGetRequest(path: String) = HttpRequest.newBuilder()
        .uri(URI.create(path))
        .GET()
        .build()

    fun getResponse(request: HttpRequest, serviceName : String): String {
        try{
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        return when(response.statusCode()) {
            in 200..204 -> response.body()
            in 300..308 -> throw ServiceRuntimeException(300_001, "Redirect detected while communicating with $serviceName service")
            in 400..499 -> throw ServiceRuntimeException(400_001, "Response code ${response.statusCode()} recived from $serviceName service. Body details: ${response.body()}")
            in 500..508 -> throw ServiceRuntimeException(500_001, "$serviceName service thrown an exception when processing the request.")
            else -> throw ServiceRuntimeException(500_002, "Unprocessable response code recived from $serviceName service. Code: ${response.statusCode()}")
            }
        } catch (ioe: IOException) {
            logger.error("IOException while sending request to $serviceName service.",ioe)
            throw ServiceRuntimeException(500_003, "IOException while sending request to $serviceName.", ioe)
        } catch (ie: InterruptedException) {
            logger.error("InterruptedException while sending request to $serviceName service.",ie)
            throw ServiceRuntimeException(500_007, "InterruptedException while sending request to $serviceName.", ie)
        }
    }

}
