package com.hluhovskyi.interviewkit

internal class Event

internal class Subscriber {

    @Subscribe
    fun observe(event: Event) {
        println("Yay, event!")
    }
}

fun main() {
    val subscriber = Subscriber()

    EventBus.default().register(subscriber)

    // should print message
    EventBus.default().post(Event())

    EventBus.default().unregister(subscriber)

    // should not print message
    EventBus.default().post(Event())
}
