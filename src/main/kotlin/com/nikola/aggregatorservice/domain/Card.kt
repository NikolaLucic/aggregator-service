package com.nikola.aggregatorservice.domain

interface Card {
     val id: Int
     val status: CardStatus
     val cardNumber: String
     val sequenceNumber: Int
     val cardHolder: String
}
