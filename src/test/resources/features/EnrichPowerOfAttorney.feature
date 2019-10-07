Feature: Enrich power of attorney

  Scenario: Get enriched power of attorney with 3 cards
    Given Stub service is running
    When Enriched power of attorney with id 1 is requested
    Then Valid enriched power of attorney is returned
    And It has 2 debit cards
    And It has 1 credit card
