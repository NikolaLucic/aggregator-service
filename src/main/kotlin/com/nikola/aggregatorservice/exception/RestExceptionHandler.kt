package com.nikola.aggregatorservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [ServiceRuntimeException::class])
    protected fun handleAggregatorServiceRuntimeException(e: ServiceRuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(e.errorCode, e.message ?: "Default error message"),
            determineHttpStatus(e.errorCode)
        )
    }

    private fun determineHttpStatus(errorCode: Int): HttpStatus = when (errorCode) {
        in 400000..400999 -> HttpStatus.BAD_REQUEST
        in 401000..401999 -> HttpStatus.UNAUTHORIZED
        in 403000..403999 -> HttpStatus.FORBIDDEN
        in 404000..404999 -> HttpStatus.NOT_FOUND
        in 500000..500999 -> HttpStatus.INTERNAL_SERVER_ERROR
        in 504000..504999 -> HttpStatus.GATEWAY_TIMEOUT
        else -> HttpStatus.INTERNAL_SERVER_ERROR
    }
}
