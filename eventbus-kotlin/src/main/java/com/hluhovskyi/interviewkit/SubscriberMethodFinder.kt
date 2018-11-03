package com.hluhovskyi.interviewkit

import java.lang.reflect.Method

object SubscriberMethodFinder {

    fun findWithAnnotation(
            target: Any,
            annotationClass: Class<out Annotation>
    ): List<SubscriberMethod> = target.javaClass.methods
            .filter { method ->
                method.getAnnotationsByType(annotationClass).size == 1
                        && method.parameterTypes.size == 1
            }
            .map { method ->
                SubscriberMethod(
                        target = target,
                        eventClass = method.parameterTypes.first(),
                        method = method
                )
            }


}

data class SubscriberMethod(
        val target: Any,
        val eventClass: Class<*>,
        private val method: Method
) {
    operator fun invoke(event: Any) {
        method.invoke(target, event)
    }
}