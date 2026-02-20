# Clean Architecture demo application

## Changes to explore

### Show the controller doing real work
* Open OrderController
* Point out: validation, entity creation, calls to RestTemplate, repository, and Kafka all mixed in one method

### Show internal leakage
* Point out that id is leaking

### Break API with a small change
* Rename a field in OrderEntity (e.g., quantity → qty)
* Show that:
* API response breaks
* Tests do not fail
* Try to fix it with jsonproperty and show why this is not a good idea

###  Show infrastructure leakage
* Highlight RestTemplate inside service
* KafkaTemplate directly in service
* Ask: “What if we switch to another inventory service?”

### Show listener data leakage
* Show that Paymentlistner is leaking status into domain layer
* Show what would need to be changed to fix it (both domain and infrastructure)