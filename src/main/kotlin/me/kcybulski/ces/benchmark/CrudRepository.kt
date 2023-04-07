package me.kcybulski.ces.benchmark

import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class CrudRepository(
    database: CoroutineDatabase
) : OrdersRepository {

    private val orders = database.getCollection<SimpleOrder>("crud_orders")

    override suspend fun new(id: String): Order =
        SimpleOrder(id, mutableListOf(), 0)

    override suspend fun save(order: Order) {
        require(order is SimpleOrder)
        val result = orders.replaceOne(
            and(SimpleOrder::id eq order.id, SimpleOrder::version eq order.version),
            order.copy(version = order.version + 1)
        )
        if (result.modifiedCount == 0L) error("Error while saving!")
    }

    override suspend fun find(id: String): Order? =
        orders.findOneById(id)
}