package com.hluhovskyi.interviewkit

import java.lang.reflect.Method

annotation class Subscribe

interface EventBus {

    fun register(subscriber: Any)

    fun unregister(subscriber: Any)

    fun post(event: Any)

    companion object {

        fun default(): EventBus = throw InstantiationError("To be implemented")
    }

}

internal class SubscriberMethod(
        val eventType: Class<*>,
        val target: Any,
        private val method: Method
) {
    operator fun invoke(event: Any) {
        method.invoke(target, event)
    }
}

internal fun findMethodsWithAnnotation(
        target: Any,
        annotationType: Class<out Annotation>
): List<SubscriberMethod> = target.javaClass.methods
        .filter { method ->
            method.getAnnotationsByType(annotationType).size == 1
                    && method.parameterTypes.size == 1
        }
        .map { method ->
            SubscriberMethod(
                    eventType = method.parameterTypes.first(),
                    target = target,
                    method = method
            )
        }