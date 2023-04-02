package me.kcybulski.ces.benchmark

interface OrdersRepository {

    suspend fun new(id: String): Order
    suspend fun save(order: Order)
    suspend fun find(id: String): Order?

}

