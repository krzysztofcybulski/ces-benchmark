package me.kcybulski.ces.benchmark

import me.kcybulski.ces.eventstore.Stream
import me.kcybulski.ces.eventstore.aggregates.AggregatePartiallySaved
import me.kcybulski.ces.eventstore.aggregates.AggregateSaved
import me.kcybulski.ces.eventstore.aggregates.AggregateSavingError
import me.kcybulski.ces.eventstore.aggregates.Aggregates

class EventSourcingOrdersRepository(
    private val aggregates: Aggregates
) : OrdersRepository {

    override suspend fun new(id: String): Order =
        EventsOrder.new(id)

    override suspend fun find(id: String): Order? =
        aggregates.load<EventsOrder>(Stream("order:$id"))

    override suspend fun save(order: Order) {
        require(order is EventsOrder)
        when(aggregates.save(order)) {
            is AggregateSaved<*> -> {}
            is AggregatePartiallySaved<*> -> {}
            AggregateSavingError -> {
                error("Error while saving!")
            }
        }
    }

}