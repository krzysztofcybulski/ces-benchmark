package me.kcybulski.ces.benchmark

import io.gatling.javaapi.core.CoreDsl.atOnceUsers
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import java.time.Duration
import java.time.temporal.ChronoUnit.MILLIS
import java.util.UUID.randomUUID
import kotlin.random.Random

val minOrderSize = 800
val maxOrderSize = 1000
val users = 50

class OrdersSimulation : Simulation() {

    val httpProtocol = http
        .baseUrl("http://localhost:5050")
        .acceptHeader("application/json")
        .doNotTrackHeader("1")

    private val esProtocol = httpProtocol
        .header("Service", "event-store")

    private val crudProtocol = httpProtocol
        .header("Service", "crud")

    private val scenario = scenario("OrdersScenario")
            .repeat { Random.nextInt(minOrderSize, maxOrderSize) }
            .on(
                exec(
                    http("addProduct")
                        .post { "/orders/${randomUUID()}/products" }
                )
                    .pause(Duration.of(10, MILLIS))
            )

    init {
        setUp(
            scenario
                .injectOpen(atOnceUsers(users))
                .protocols(esProtocol)
        )
    }
}