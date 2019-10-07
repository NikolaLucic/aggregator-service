package com.nikola.aggregatorservice.domain

import com.google.gson.Gson

data class PowerOfAttorney (val id: Int,
                            val grantor: String,
                            val grantee: String,
                            val account: String,
                            val direction: String,
                            val authorizations: List<Authorization> = listOf(),
                            val cards: List<CardSummary>? = listOf()) {

    fun asJson() = Gson().toJson(this)
}
