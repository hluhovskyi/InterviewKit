package com.hluhovskyi.interviewkit

annotation class Subscribe

interface EventBus {

    fun register(subscriber: Any)

    fun unregister(subscriber: Any)

    fun post(event: Any)

    companion object {

        fun getInstance(): EventBus = throw InstantiationError("To be implemented")
    }
}
