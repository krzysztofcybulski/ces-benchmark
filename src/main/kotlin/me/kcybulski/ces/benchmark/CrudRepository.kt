package me.kcybulski.ces.benchmark

import org.litote.kmongo.coroutine.CoroutineDatabase

class CrudRepository(
    database: CoroutineDatabase
) : OrdersRepository {

    private val orders = database.getCollection<SimpleOrder>("crud_orders")

    override suspend fun new(id: String): Order =
        SimpleOrder(id, mutableListOf())

    override suspend fun save(order: Order) {
        require(order is SimpleOrder)
        orders.save(order)
    }

    override suspend fun find(id: String): Order? =
        orders.findOneById(id)
}