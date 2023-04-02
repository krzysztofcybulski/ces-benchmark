package me.kcybulski.ces.benchmark

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.kcybulski.ces.client.CloudStoreClientFactory.cloudStoreClient
import me.kcybulski.ces.eventstore.EventStoreConfiguration
import me.kcybulski.ces.eventstore.aggregates.Aggregates
import net.datafaker.Faker
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import ratpack.core.handling.Context
import ratpack.core.server.RatpackServer

fun main() {

    val faker = Faker()
    val scope = CoroutineScope(Dispatchers.IO)

    val database = KMongo
        .createClient("mongodb://localhost:27017")
        .coroutine
        .getDatabase("event-store")

    val eventStore = cloudStoreClient {
        address = "localhost:5000"
    }

    val repositoryFactory = ContextBasedRepositoryFactory(
        CrudRepository(database),
        EventSourcingOrdersRepository(Aggregates.onEventStore(eventStore))
    )

    RatpackServer.start { server ->
        server
            .handlers { chain ->
                chain
                    .post("orders/:orderId/products") { ctx ->
                        ctx.renderCoroutine(scope) {
                            val order: Order = repositoryFactory[ctx].find(ctx.orderId)
                                ?: repositoryFactory[ctx].new(ctx.orderId)
                            order.addProduct(faker.food().ingredient())
                            repositoryFactory[ctx].save(order)
                            order
                        }
                    }
                    .get("orders/:orderId") { ctx ->
                        ctx.renderCoroutine(scope) {
                            repositoryFactory[ctx].find(ctx.orderId)
                        }
                    }
            }
    }
}

private val Context.orderId: String
    get() = pathTokens["orderId"] ?: error("No orderId found in path")