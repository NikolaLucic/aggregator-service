package com.nikola.aggregatorservice.controller

import com.nikola.aggregatorservice.api.EnrichedPowerOfAttorney
import com.nikola.aggregatorservice.service.CreditCardService
import com.nikola.aggregatorservice.service.DebitCardService
import com.nikola.aggregatorservice.service.PowerOfAttorneyService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.lang.NonNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Api("enriched-power-of-attorney")
@RestController
@RequestMapping("/v1/enriched-power-of-attorney", produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class EnrichedPOAController(
    val poaService: PowerOfAttorneyService,
    val debitCardService: DebitCardService,
    val creditCardService: CreditCardService
) {

    @ApiOperation(value = "Returns enriched power of attourney which contains also card details.")
    @GetMapping
    fun getPOAById(@NonNull @ApiParam("Unique identifier of POA") @RequestParam("id") id: Int): ResponseEntity<EnrichedPowerOfAttorney> =
        runBlocking {
            val poa = poaService.getPowerOfAttorneyById(id)

            val debitCards = debitCardService.getDebitCardDetailsAsync(poa)
            val creditCards = creditCardService.getCreditCardDetailsAsync(poa)

            poa?.let {
                ResponseEntity(EnrichedPowerOfAttorney(poa, debitCards.await(), creditCards.await()), HttpStatus.OK)
            } ?: ResponseEntity(HttpStatus.NOT_FOUND)
        }


}
