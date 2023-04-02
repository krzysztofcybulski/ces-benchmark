package me.kcybulski.ces.benchmark

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ratpack.core.handling.Context
import ratpack.core.jackson.Jackson.json
import ratpack.exec.Promise

fun <T> Context.renderCoroutine(coroutineScope: CoroutineScope, handler: suspend () -> T) =
    Promise.async { downstream ->
        coroutineScope.launch {
            try {
                downstream.success(handler())
            } catch(e: Throwable) {
                downstream.error(e)
            }
        }
    }
        .onError {
            response.status(500).send()
        }
        .then {
            if (it == null) {
                notFound()
            } else {
                render(json(it))
            }
        }