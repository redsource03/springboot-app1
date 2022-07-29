Feature:  This contains should consume message from topic 'spring-app1-topic' and publish to 'spring-app2-topic'

  Scenario: Received a message from topic 'spring-app1-topic'
    Given Collection is empty
    * Wait-Time: 10000 ms is over
    * A Message is published to topic:spring-app1-topic with payload:payload
    * Wait-Time: 10000 ms is over
    Then Payload should be saved in the collection times:2