package com.nikola.aggregatorservice.exception

import java.lang.RuntimeException

class ServiceRuntimeException: RuntimeException {

    var errorCode: Int = 0

    constructor(errorCode: Int) : super() {
        this.errorCode = errorCode
    }

    constructor(errorCode: Int, message: String) : super(message) {
        this.errorCode = errorCode
    }

    constructor( errorCode: Int, message: String?, cause: Throwable?) : super(message, cause) {
        this.errorCode = errorCode
    }


}
