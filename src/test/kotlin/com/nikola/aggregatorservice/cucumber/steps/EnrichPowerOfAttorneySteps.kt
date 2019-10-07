package com.nikola.aggregatorservice.cucumber.steps

import com.google.gson.Gson
import com.nikola.aggregatorservice.api.EnrichedPowerOfAttorney
import com.nikola.aggregatorservice.cucumber.SpringBootCucumberTestConfig
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(classes = [SpringBootCucumberTestConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = ["server.port=8081"])
class EnrichPowerOfAttorneySteps {

    @Value("\${server.port}")
    lateinit var appPort: String

    @Autowired
    lateinit var httpClient: HttpClient

    lateinit var result : EnrichedPowerOfAttorney

    @Given("^Stub service is running$")
    fun checkIfStubIsRunning() {
        val checkRequest = createGetRequest("http://localhost:8080/power-of-attorneys/0001")
        val response = httpClient.send(checkRequest, HttpResponse.BodyHandlers.ofString())
        assertNotNull(response)
        assertEquals(200, response.statusCode())
    }

    @When("^Enriched power of attorney with id (\\d+) is requested$")
    fun retrivePOAWithId(id: Int){
        var enrichedPOARequest = createGetRequest("http://localhost:$appPort/v1/enriched-power-of-attorney?id=$id")
        val response = httpClient.send(enrichedPOARequest, HttpResponse.BodyHandlers.ofString())
        result = Gson().fromJson(response.body(), EnrichedPowerOfAttorney::class.java)
    }

    @Then("^Valid enriched power of attorney is returned$")
    fun checkEnrichedPOA(){
        assertNotNull(result, "Returned null as enriched POA!")
        assertNotNull(result.id, "ID should never be null!")
        assertNotNull(result.grantee, "Grantee should never be null!")
        assertNotNull(result.grantor, "Grantor should never be null!")
        assertTrue(result.messages.isEmpty(), "There are error messages in the response!")
    }

    @And("^It has (\\d+) debit cards$")
    fun checkDebitCardsCount(cardsCount: Int) {
        assertEquals(cardsCount, result.debitCards.size)
    }

    @And("^It has (\\d+) credit card$")
    fun checkCreditCardsCount(cardsCount: Int) {
        assertEquals(cardsCount, result.creditCards.size)
    }

    fun createGetRequest(path: String) = HttpRequest.newBuilder()
        .uri(URI.create(path))
        .GET()
        .build()

}
