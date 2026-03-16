# Clean Architecture demo application

## Changes to explore

### Show the controller doing real work
* Open OrderController
* Point out: validation, entity creation, calls to RestTemplate, repository, all mixed in one method
* Point out that id is leaking

### Break API with a small change
* Rename a field in OrderEntity (e.g., quantity → qty)
* Show that:
* API response breaks
* Tests do not fail
* Try to fix it with jsonproperty and show why this is not a good idea

###  Show infrastructure leakage
* Highlight RestTemplate inside service
* Ask: “What if we switch to another inventory service?”

## Steps to refactor
1. Set Move OrderService to application layer.
2. Refactor orderController
   * Return an order domain object instead of OrderEntity
   * And map optionally to an orderDto
   * Debate wether to move validation to service layer.
   * move controller to adapter layer.
3. Fix PaymentListener
   * Move repository call to service.
   * Pull up mapping of status from service to PaymentListener.
4. Fix restTemplate
   * Move restTemplate call to a restclient and put the adapter in the right package.
5. Fix KafkaTemplate
   * Change signature from <String, OrderEntity to <String, Order>
   * Translate OrderEntity to Order for sending.
   * Point out that status change is still done on the domain object whereas it is only a concern for the outgoing message.
   * Create a class KafkaPublisher that receives an Order and publishes it.
   * Move KafkaPublisher and config to infrastructure layer and introduce an interface.
6. Introduce OrderRepository as a step between the service and the jpa repository.
   * Create OrderRepository class in the right adapter package.
   * Do the mapping from OrderEntity to Order and vice versa in that class.
   