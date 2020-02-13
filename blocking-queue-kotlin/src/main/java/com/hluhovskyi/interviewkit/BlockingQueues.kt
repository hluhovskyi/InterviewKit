package com.hluhovskyi.interviewkit

interface Queue<T> {

    fun put(item: T)

    fun take(): T
}

object BlockingQueues {

    fun <T> create(capacity: Int): Queue<T> {
        throw IllegalStateException("Good luck!")
    }
}
