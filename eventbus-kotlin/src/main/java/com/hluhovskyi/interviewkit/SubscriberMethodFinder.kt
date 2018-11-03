package com.hluhovskyi.interviewkit

import java.lang.reflect.Method

object SubscriberMethodFinder {

    fun findWithAnnotation(
            subscriber: Any,
            annotationClass: Class<out Annotation>
    ): List<SubscriberMethod> = subscriber.javaClass.methods
            .filter { method ->
                method.getAnnotationsByType(annotationClass).size == 1
                        && method.parameterTypes.size == 1
            }
            .map { method ->
                SubscriberMethod(
                        subscriber = subscriber,
                        eventClass = method.parameterTypes.first(),
                        method = method
                )
            }
}

data class SubscriberMethod(
        val subscriber: Any,
        val eventClass: Class<*>,
        private val method: Method
) {
    operator fun invoke(event: Any) {
        method.invoke(subscriber, event)
    }
}
