package me.kcybulski.ces.benchmark

import me.kcybulski.ces.eventstore.Event
import me.kcybulski.ces.eventstore.SimpleEvent
import me.kcybulski.ces.eventstore.Stream
import me.kcybulski.ces.eventstore.aggregates.Aggregate
import me.kcybulski.ces.eventstore.aggregates.AggregateCreator
import org.bson.codecs.pojo.annotations.BsonId

sealed interface Order {

    val id: String
    val products: List<String>

    fun addProduct(product: String)
}

class SimpleOrder(
    @BsonId
    override val id: String,
    override val products: MutableList<String>
) : Order {

    override fun addProduct(product: String) {
        products += product
    }

}

class EventsOrder(
    override val id: String,
    override val products: MutableList<String>
) : Order, Aggregate<EventsOrder>() {

    override val stream = Stream("order:$id")

    override fun addProduct(product: String) {
        event(AddProductEvent(id, product))
    }

    override fun apply(event: Event<*>): EventsOrder {
        when (event) {
            is AddProductEvent -> products += event.product
        }
        return this
    }

    companion object: AggregateCreator<EventsOrder, NewOrderEvent> {

        override fun from(event: NewOrderEvent): EventsOrder =
            EventsOrder(event.orderId, mutableListOf())

        fun new(id: String): EventsOrder =
            EventsOrder(id, mutableListOf())
                .event(NewOrderEvent(id))

    }
}

class NewOrderEvent(
    val orderId: String
): SimpleEvent()

data class AddProductEvent(
    val orderId: String,
    val product: String
): SimpleEvent()