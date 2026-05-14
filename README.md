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
   
## Prompts
Replace the repository call in getAllOrders in the OrderController to a call to the OrderService.
Also create the function in the service.

Both functions in the OrderService return the OrderEntity to the controller.
Replace the return type with an OrderDto record that is then mapped from the entity.

n the order controller, map the orderDto to an OrderResponse record class and use this type as the return type.

wrap the restTemplate call in a separate Rest adapter component class
and let the OrderService make use of this component class.
Also move the inStock check there.
The function in this class should now just return a boolean if a product is in stock.

Extract an interface from the StockAdapter and use that in the OrderService.

Now refactor the OrderRepository interface into a Spring Repository
component called  "OrderRepository".
Rename the interface to OrderJpaRepository.
Let the new repository component return not the entities,
but a domain class called Order (which is a record),
so the OrderService doesn't know about the entities anymore.
Do the mapping inside the new repository component.