package me.kcybulski.ces.benchmark

import ratpack.core.handling.Context
import kotlin.jvm.optionals.getOrNull

class ContextBasedRepositoryFactory(
    private val crudRepository: OrdersRepository,
    private val eventSourcingRepository: OrdersRepository
) {

    operator fun get(ctx: Context): OrdersRepository =
        when (serviceName(ctx)) {
            "eventstore", "event-store", "es" -> eventSourcingRepository
            else -> crudRepository
        }

    private fun serviceName(ctx: Context) = ctx
        .header("Service")
        .getOrNull()
        ?.lowercase()

}