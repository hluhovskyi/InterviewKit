package com.hluhovskyi.interviewkit

import java.lang.reflect.Method

object SubscriberMethodFinder {

    /**
     * Returns list of method defined in given `subscriber` which have only one input parameter
     * and are annotated with `annotationClass` annotation.
     */
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

/**
 * Class which holds all necessary information to implement subscription.
 * <p>
 * To invoke method with some event call `invoke(event)`
 */
data class SubscriberMethod(
        val subscriber: Any,
        val eventClass: Class<*>,
        private val method: Method
) {
    operator fun invoke(event: Any) {
        method.invoke(subscriber, event)
    }
}
