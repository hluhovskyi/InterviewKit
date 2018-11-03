package com.hluhovskyi.interviewkit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SubscriberMethodFinder {

    /**
     * Returns list of method defined in given `subscriber` which have only one input parameter
     * and are annotated with `annotationClass` annotation.
     */
    static List<SubscriberMethod> findAnnotatedMethods(
            Object subscriber,
            Class<? extends Annotation> annotationClass
    ) {
        return Stream.of(subscriber.getClass().getMethods())
                .filter(method -> method.getAnnotationsByType(annotationClass).length == 1
                        && method.getGenericParameterTypes().length == 1
                )
                .map(method -> new SubscriberMethod(
                        subscriber,
                        method.getParameterTypes()[0],
                        method)
                )
                .collect(Collectors.toList());
    }
}

/**
 * Class which holds all necessary information to implement subscription.
 * <p>
 * To invoke method with some event call `invoke(event)`
 */
class SubscriberMethod {

    final Object subscriber;
    final Class<?> eventClass;
    private final Method method;

    SubscriberMethod(Object subscriber, Class<?> eventClass, Method method) {
        this.eventClass = eventClass;
        this.subscriber = subscriber;
        this.method = method;
    }

    void invoke(Object event) {
        try {
            method.invoke(subscriber, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}